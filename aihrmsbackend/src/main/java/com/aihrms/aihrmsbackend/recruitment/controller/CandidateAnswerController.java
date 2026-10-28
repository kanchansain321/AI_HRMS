package com.aihrms.aihrmsbackend.recruitment.controller;

import com.aihrms.aihrmsbackend.dto.response.CustomResponse;
import com.aihrms.aihrmsbackend.recruitment.dto.request.CandidateAnswerRequest;
import com.aihrms.aihrmsbackend.recruitment.service.CandidateAnswerService;
import com.aihrms.aihrmsbackend.security.RequireAdmin;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/recruitment/answers")
@RequireAdmin
public class CandidateAnswerController {

    private final CandidateAnswerService candidateAnswerService;

    public CandidateAnswerController(CandidateAnswerService candidateAnswerService) {
        this.candidateAnswerService = candidateAnswerService;
    }

    @PostMapping
    public ResponseEntity<?> submit(@RequestBody CandidateAnswerRequest request) {
        CustomResponse response = candidateAnswerService.submitAnswer(request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping
    public ResponseEntity<?> list(@RequestParam(required = false) Long questionId,
                                  @RequestParam(required = false) Long resumeId) {
        CustomResponse response;
        if (questionId != null) {
            response = candidateAnswerService.listByQuestion(questionId);
        } else if (resumeId != null) {
            response = candidateAnswerService.listByResume(resumeId);
        } else {
            response = new CustomResponse(400, null, "Provide questionId or resumeId");
        }
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}