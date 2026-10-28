package com.aihrms.aihrmsbackend.recruitment.service.impl;

import com.aihrms.aihrmsbackend.dto.response.CustomResponse;
import com.aihrms.aihrmsbackend.exception.ResourceNotFoundException;
import com.aihrms.aihrmsbackend.recruitment.dto.request.CandidateAnswerRequest;
import com.aihrms.aihrmsbackend.recruitment.entity.CandidateAnswer;
import com.aihrms.aihrmsbackend.recruitment.entity.InterviewQuestion;
import com.aihrms.aihrmsbackend.recruitment.repository.CandidateAnswerRepo;
import com.aihrms.aihrmsbackend.recruitment.repository.InterviewQuestionRepo;
import com.aihrms.aihrmsbackend.recruitment.service.CandidateAnswerService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CandidateAnswerServiceImpl implements CandidateAnswerService {

    private final CandidateAnswerRepo candidateAnswerRepo;
    private final InterviewQuestionRepo interviewQuestionRepo;

    public CandidateAnswerServiceImpl(CandidateAnswerRepo candidateAnswerRepo, InterviewQuestionRepo interviewQuestionRepo) {
        this.candidateAnswerRepo = candidateAnswerRepo;
        this.interviewQuestionRepo = interviewQuestionRepo;
    }

    @Override
    @Transactional
    public CustomResponse submitAnswer(CandidateAnswerRequest request) {
        if (request == null || request.getQuestionId() == null) {
            return new CustomResponse(HttpStatus.BAD_REQUEST.value(), null, "questionId is required");
        }
        if (request.getAnswerText() == null || request.getAnswerText().isBlank()) {
            return new CustomResponse(HttpStatus.BAD_REQUEST.value(), null, "answerText is required");
        }
        InterviewQuestion question = interviewQuestionRepo.findById(request.getQuestionId())
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + request.getQuestionId()));
        CandidateAnswer answer = new CandidateAnswer();
        answer.setQuestion(question);
        answer.setAnswerText(request.getAnswerText().trim());
        return new CustomResponse(HttpStatus.CREATED.value(), toMap(candidateAnswerRepo.save(answer)), "Answer submitted");
    }

    @Override
    public CustomResponse listByQuestion(Long questionId) {
        if (!interviewQuestionRepo.existsById(questionId)) {
            throw new ResourceNotFoundException("Question not found with id: " + questionId);
        }
        List<Map<String, Object>> list = candidateAnswerRepo.findByQuestionId(questionId).stream().map(this::toMap).collect(Collectors.toList());
        CustomResponse response = new CustomResponse(HttpStatus.OK.value(), list, "Answers fetched");
        response.setSize(list.size());
        return response;
    }

    @Override
    public CustomResponse listByResume(Long resumeId) {
        List<Map<String, Object>> list = candidateAnswerRepo.findByQuestionResumeId(resumeId).stream().map(this::toMap).collect(Collectors.toList());
        CustomResponse response = new CustomResponse(HttpStatus.OK.value(), list, "Answers fetched");
        response.setSize(list.size());
        return response;
    }

    private Map<String, Object> toMap(CandidateAnswer a) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", a.getId());
        m.put("questionId", a.getQuestion() != null ? a.getQuestion().getId() : null);
        m.put("answerText", a.getAnswerText());
        return m;
    }
}