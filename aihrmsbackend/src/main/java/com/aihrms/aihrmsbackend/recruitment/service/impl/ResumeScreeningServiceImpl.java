package com.aihrms.aihrmsbackend.recruitment.service.impl;

import com.aihrms.aihrmsbackend.ai.dto.GeneratedQuestion;
import com.aihrms.aihrmsbackend.ai.util.ResumeTextExtractor;
import com.aihrms.aihrmsbackend.dto.response.CustomResponse;
import com.aihrms.aihrmsbackend.exception.ResourceNotFoundException;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(ResumeScreeningServiceImpl.class);

    @Autowired
    private EmpResumeRepo empResumeRepo;
    @Autowired
    private EmpSkillsRepo empSkillsRepo;
    @Autowired
    private InterviewQuestionRepo interviewQuestionRepo;
    @Autowired
    private AiServiceClient aiServiceClient;
    @Autowired
    private ResumeTextExtractor resumeTextExtractor;
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
            Path target = dir.resolve(UUID.randomUUID() + "_" + original.replaceAll("\\s+", "_"));
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

            EmpResume resume = new EmpResume();
            resume.setCandidateName(candidateName.trim());
            resume.setEmail(email.trim());
            resume.setFilePath(target.toAbsolutePath().toString());
            EmpResume saved = empResumeRepo.save(resume);
            if (skills != null && !skills.isEmpty()) {
                saveSkills(saved, skills);
            }
            return new CustomResponse(HttpStatus.CREATED.value(), toResumeResponse(saved), "Resume uploaded successfully");
        } catch (IOException e) {
            return new CustomResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), null, "Failed to store resume: " + e.getMessage());
        } catch (Exception e) {
            return new CustomResponse(HttpStatus.BAD_REQUEST.value(), null, e.getMessage());
        }
    }

    @Override
    public CustomResponse listResumes() {
        List<ResumeResponse> list = empResumeRepo.findAll().stream().map(this::toResumeResponse).collect(Collectors.toList());
        CustomResponse response = new CustomResponse(HttpStatus.OK.value(), list, "Resumes fetched successfully");
        response.setSize(list.size());
        return response;
    }

    @Override
    public CustomResponse getResume(Long resumeId) {
        EmpResume resume = empResumeRepo.findById(resumeId)
                .orElseThrow(() -> new ResourceNotFoundException("Resume not found with id: " + resumeId));
        return new CustomResponse(HttpStatus.OK.value(), toResumeResponse(resume), "Resume fetched successfully");
    }

    @Override
    @Transactional
    public CustomResponse screenResume(Long resumeId, List<String> skills, String resumeText) {
        long start = System.currentTimeMillis();
        EmpResume resume = empResumeRepo.findById(resumeId)
                .orElseThrow(() -> new ResourceNotFoundException("Resume not found with id: " + resumeId));

        List<String> extracted;
        if (skills != null && !skills.isEmpty()) {
            extracted = skills;
        } else if (resumeText != null && !resumeText.isBlank()) {
            extracted = aiServiceClient.extractSkills(resumeText);
        } else {
            // Prefer local PDF/text extract + fast text model; multimodal file upload is last resort.
            String textFromFile = "";
            try {
                textFromFile = resumeTextExtractor.extractFromPath(resume.getFilePath());
            } catch (IOException e) {
                log.warn("Resume text extract failed for {}: {}", resumeId, e.getMessage());
            }
            extracted = (textFromFile != null && !textFromFile.isBlank())
                    ? aiServiceClient.extractSkills(textFromFile)
                    : aiServiceClient.extractSkillsFromFile(resume.getFilePath());
        }

        if (extracted.isEmpty()) {
            return new CustomResponse(HttpStatus.BAD_REQUEST.value(), null,
                    "No skills found. Ensure GOOGLE_API_KEY is set, or pass skills / resumeText.");
        }
        empSkillsRepo.deleteByResumeId(resumeId);
        saveSkills(resume, extracted);
        log.info("screenResume resumeId={} skills={} totalMs={}",
                resumeId, extracted.size(), System.currentTimeMillis() - start);
        return new CustomResponse(HttpStatus.OK.value(), new SkillExtractionResponse(resumeId, extracted), "Resume screened successfully");
    }

    @Override
    @Transactional
    public CustomResponse generateInterviewQuestions(GenerateQuestionsRequest request) {
        if (request == null || request.getResumeId() == null) {
            return new CustomResponse(HttpStatus.BAD_REQUEST.value(), null, "resumeId is required");
        }
        EmpResume resume = empResumeRepo.findById(request.getResumeId())
                .orElseThrow(() -> new ResourceNotFoundException("Resume not found with id: " + request.getResumeId()));
        List<String> skills = empSkillsRepo.findByResumeId(resume.getId()).stream()
                .map(EmpSkills::getSkillName).collect(Collectors.toList());
        if (skills.isEmpty()) {
            return new CustomResponse(HttpStatus.BAD_REQUEST.value(), null, "No skills on this resume. Screen the resume first.");
        }
        // Always regenerate on explicit generate request (old cache was trapping weak fallbacks).
        interviewQuestionRepo.deleteByResumeId(resume.getId());
        List<GeneratedQuestion> generated = aiServiceClient.generateQuestions(skills, request.getCount());
        if (generated == null || generated.isEmpty()) {
            return new CustomResponse(HttpStatus.BAD_GATEWAY.value(), null,
                    "Could not generate interview questions. Check Gemini configuration and try again.");
        }
        List<InterviewQuestionResponse> saved = new ArrayList<>();
        for (GeneratedQuestion gq : generated) {
            InterviewQuestion q = new InterviewQuestion();
            q.setResume(resume);
            q.setSkillName(gq.getSkill());
            q.setQuestionText(gq.getQuestion());
            saved.add(toQuestionResponse(interviewQuestionRepo.save(q)));
        }
        return new CustomResponse(HttpStatus.CREATED.value(), saved, "Interview questions generated");
    }

    @Override
    public CustomResponse listInterviewQuestions(Long resumeId) {
        if (!empResumeRepo.existsById(resumeId)) {
            throw new ResourceNotFoundException("Resume not found with id: " + resumeId);
        }
        List<InterviewQuestionResponse> list = interviewQuestionRepo.findByResumeId(resumeId).stream()
                .map(this::toQuestionResponse).collect(Collectors.toList());
        CustomResponse response = new CustomResponse(HttpStatus.OK.value(), list, "Interview questions fetched");
        response.setSize(list.size());
        return response;
    }

    private void saveSkills(EmpResume resume, List<String> skills) {
        for (String skill : skills) {
            if (skill == null || skill.isBlank()) continue;
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
        r.setSkills(empSkillsRepo.findByResumeId(resume.getId()).stream().map(EmpSkills::getSkillName).collect(Collectors.toList()));
        return r;
    }

    private InterviewQuestionResponse toQuestionResponse(InterviewQuestion q) {
        InterviewQuestionResponse r = new InterviewQuestionResponse();
        r.setId(q.getId());
        r.setResumeId(q.getResume() != null ? q.getResume().getId() : null);
        r.setSkillName(q.getSkillName());
        r.setQuestionText(q.getQuestionText());
        return r;
    }
}