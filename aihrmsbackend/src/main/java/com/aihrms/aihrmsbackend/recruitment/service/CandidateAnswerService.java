package com.aihrms.aihrmsbackend.recruitment.service;

import com.aihrms.aihrmsbackend.dto.response.CustomResponse;
import com.aihrms.aihrmsbackend.recruitment.dto.request.CandidateAnswerRequest;

public interface CandidateAnswerService {
    CustomResponse submitAnswer(CandidateAnswerRequest request);
    CustomResponse listByQuestion(Long questionId);
    CustomResponse listByResume(Long resumeId);
}