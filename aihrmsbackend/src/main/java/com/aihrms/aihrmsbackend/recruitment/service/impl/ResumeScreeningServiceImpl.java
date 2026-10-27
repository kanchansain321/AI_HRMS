/////////////////////////////////////////////////////////////////////impl//////////////////////////////////////////////////////////////////



package com.aihrms.aihrmsbackend.recruitment.service.impl;

import com.aihrms.aihrmsbackend.dto.response.CustomResponse;
import com.aihrms.aihrmsbackend.recruitment.client.AiServiceClient;
import com.aihrms.aihrmsbackend.recruitment.dto.request.GenerateQuestionsRequest;
import com.aihrms.aihrmsbackend.recruitment.dto.response.InterviewQuestionResponse;
import com.aihrms.aihrmsbackend.recruitment.dto.response.ResumeResponse;
import com.aihrms.aihrmsbackend.recruitment.dto.response.SkillExtractionResponse;
import com.aihrms.aihrmsbackend.recruitment.entity.EmpResume;
import com.aihrms.aihrmsbackend.recruitment.entity.EmpSkills;
import com.aihrms.aihrmsbackend.recruitment.entity.InterviewQuestion;
import com.aihrms.aihrmsbackend.recruitment.repository.EmpResumeRepo;
import com.aihrms.aihrmsbackend.recruitment.repository.EmpSkillsRepo;
import com.aihrms.aihrmsbackend.recruitment.repository.InterviewQuestionRepo;
import com.aihrms.aihrmsbackend.recruitment.service.ResumeScreeningService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ResumeScreeningServiceImpl implements ResumeScreeningService {

    @Autowired
    private EmpResumeRepo empResumeRepo;

    @Autowired
    private EmpSkillsRepo empSkillsRepo;

    @Autowired
    private InterviewQuestionRepo interviewQuestionRepo;

    @Autowired
    private AiServiceClient aiServiceClient;

    @Value("${recruitment.resume.upload-dir}")
    private String uploadDir;

    @Override
    @Transactional
    public CustomResponse uploadResume(String candidateName, String email, MultipartFile file, List<String> skills) {
        try {
            if (candidateName == null || candidateName.isBlank()) {
                return new CustomResponse(HttpStatus.BAD_REQUEST.value(), null, "candidateName is required");
            }
            if (email == null || email.isBlank()) {
                return new CustomResponse(HttpStatus.BAD_REQUEST.value(), null, "email is required");
            }
            if (file == null || file.isEmpty()) {
                return new CustomResponse(HttpStatus.BAD_REQUEST.value(), null, "resume file is required");
            }

            Path dir = Paths.get(uploadDir);
            Files.createDirectories(dir);

            String original = file.getOriginalFilename() != null ? file.getOriginalFilename() : "resume";
            String storedName = UUID.randomUUID() + "_" + original.replaceAll("\\s+", "_");
            Path target = dir.resolve(storedName);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

            EmpResume resume = new EmpResume();
            resume.setCandidateName(candidateName.trim());
            resume.setEmail(email.trim());
            resume.setFilePath(target.toAbsolutePath().toString());
            EmpResume saved = empResumeRepo.save(resume);

            if (skills != null && !skills.isEmpty()) {
                saveSkills(saved, skills);
            }

            return new CustomResponse(
                    HttpStatus.CREATED.value(),
                    toResumeResponse(saved),
                    "Resume uploaded successfully"
            );
        } catch (IOException e) {
            return new CustomResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), null, "Failed to store resume: " + e.getMessage());
        } catch (Exception e) {
            return new CustomResponse(HttpStatus.BAD_REQUEST.value(), null, e.getMessage());
        }
    }

    @Override
    public CustomResponse listResumes() {
        List<ResumeResponse> list = empResumeRepo.findAll().stream()
                .map(this::toResumeResponse)
                .collect(Collectors.toList());
        CustomResponse response = new CustomResponse(HttpStatus.OK.value(), list, "Resumes fetched successfully");
        response.setSize(list.size());
        return response;
    }

    @Override
    public CustomResponse getResume(Long resumeId) {
        try {
            EmpResume resume = empResumeRepo.findById(resumeId)
                    .orElseThrow(() -> new RuntimeException("Resume not found with id: " + resumeId));
            return new CustomResponse(HttpStatus.OK.value(), toResumeResponse(resume), "Resume fetched successfully");
        } catch (RuntimeException e) {
            return new CustomResponse(HttpStatus.NOT_FOUND.value(), null, e.getMessage());
        }
    }

    @Override
    @Transactional
    public CustomResponse screenResume(Long resumeId, List<String> skills, String resumeText) {
        try {
            EmpResume resume = empResumeRepo.findById(resumeId)
                    .orElseThrow(() -> new RuntimeException("Resume not found with id: " + resumeId));

            List<String> extracted = skills != null && !skills.isEmpty()
                    ? skills
                    : aiServiceClient.extractSkills(resumeText != null ? resumeText : "");

            if (extracted.isEmpty()) {
                return new CustomResponse(
                        HttpStatus.BAD_REQUEST.value(),
                        null,
                        "No skills found. Pass skills in the request, or enable ai.service and provide resumeText."
                );
            }

            empSkillsRepo.deleteByResumeId(resumeId);
            saveSkills(resume, extracted);

            SkillExtractionResponse body = new SkillExtractionResponse(resumeId, extracted);
            return new CustomResponse(HttpStatus.OK.value(), body, "Resume screened successfully");
        } catch (RuntimeException e) {
            return new CustomResponse(HttpStatus.BAD_REQUEST.value(), null, e.getMessage());
        }
    }

    @Override
    @Transactional
    public CustomResponse generateInterviewQuestions(GenerateQuestionsRequest request) {
        try {
            if (request == null || request.getResumeId() == null) {
                return new CustomResponse(HttpStatus.BAD_REQUEST.value(), null, "resumeId is required");
            }

            EmpResume resume = empResumeRepo.findById(request.getResumeId())
                    .orElseThrow(() -> new RuntimeException("Resume not found with id: " + request.getResumeId()));

            List<String> skills = empSkillsRepo.findByResumeId(resume.getId()).stream()
                    .map(EmpSkills::getSkillName)
                    .collect(Collectors.toList());

            if (skills.isEmpty()) {
                return new CustomResponse(
                        HttpStatus.BAD_REQUEST.value(),
                        null,
                        "No skills on this resume. Screen the resume first."
                );
            }

            // Cache: reuse existing questions instead of re-calling AI
            List<InterviewQuestion> existing = interviewQuestionRepo.findByResumeId(resume.getId());
            if (!existing.isEmpty()) {
                List<InterviewQuestionResponse> cached = existing.stream()
                        .map(this::toQuestionResponse)
                        .collect(Collectors.toList());
                return new CustomResponse(HttpStatus.OK.value(), cached, "Interview questions (cached)");
            }

            List<String> generated = aiServiceClient.generateQuestions(skills, request.getCount());
            List<InterviewQuestionResponse> saved = new ArrayList<>();
            for (String text : generated) {
                InterviewQuestion q = new InterviewQuestion();
                q.setResume(resume);
                q.setQuestionText(text);
                saved.add(toQuestionResponse(interviewQuestionRepo.save(q)));
            }

            return new CustomResponse(HttpStatus.CREATED.value(), saved, "Interview questions generated");
        } catch (RuntimeException e) {
            return new CustomResponse(HttpStatus.BAD_REQUEST.value(), null, e.getMessage());
        }
    }

    @Override
    public CustomResponse listInterviewQuestions(Long resumeId) {
        try {
            if (!empResumeRepo.existsById(resumeId)) {
                return new CustomResponse(HttpStatus.NOT_FOUND.value(), null, "Resume not found with id: " + resumeId);
            }
            List<InterviewQuestionResponse> list = interviewQuestionRepo.findByResumeId(resumeId).stream()
                    .map(this::toQuestionResponse)
                    .collect(Collectors.toList());
            CustomResponse response = new CustomResponse(HttpStatus.OK.value(), list, "Interview questions fetched");
            response.setSize(list.size());
            return response;
        } catch (Exception e) {
            return new CustomResponse(HttpStatus.BAD_REQUEST.value(), null, e.getMessage());
        }
    }

    private void saveSkills(EmpResume resume, List<String> skills) {
        for (String skill : skills) {
            if (skill == null || skill.isBlank()) {
                continue;
            }
            EmpSkills entity = new EmpSkills();
            entity.setResume(resume);
            entity.setSkillName(skill.trim());
            empSkillsRepo.save(entity);
        }
    }

    private ResumeResponse toResumeResponse(EmpResume resume) {
        ResumeResponse r = new ResumeResponse();
        r.setId(resume.getId());
        r.setCandidateName(resume.getCandidateName());
        r.setEmail(resume.getEmail());
        r.setFilePath(resume.getFilePath());
        r.setUploadedAt(resume.getUploadedAt());
        r.setSkills(empSkillsRepo.findByResumeId(resume.getId()).stream()
                .map(EmpSkills::getSkillName)
                .collect(Collectors.toList()));
        return r;
    }

    private InterviewQuestionResponse toQuestionResponse(InterviewQuestion q) {
        InterviewQuestionResponse r = new InterviewQuestionResponse();
        r.setId(q.getId());
        r.setResumeId(q.getResume() != null ? q.getResume().getId() : null);
        r.setQuestionText(q.getQuestionText());
        return r;
    }
}