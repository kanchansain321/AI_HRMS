///////////////////////////////////////////////////////service///////////////////////////////////////////


package com.aihrms.aihrmsbackend.recruitment.service;

import com.aihrms.aihrmsbackend.dto.response.CustomResponse;
import com.aihrms.aihrmsbackend.recruitment.dto.request.GenerateQuestionsRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ResumeScreeningService {

    CustomResponse uploadResume(String candidateName, String email, MultipartFile file, List<String> skills);

    CustomResponse listResumes();

    CustomResponse getResume(Long resumeId);

    /** Extract / persist skills for a resume (AI client or provided skills). */
    CustomResponse screenResume(Long resumeId, List<String> skills, String resumeText);

    CustomResponse generateInterviewQuestions(GenerateQuestionsRequest request);

    CustomResponse listInterviewQuestions(Long resumeId);
}