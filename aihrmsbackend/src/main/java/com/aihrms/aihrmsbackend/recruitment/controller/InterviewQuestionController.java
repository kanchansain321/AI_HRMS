//////////////////////////////////////////controller////////////////////////////////////////////////



package com.aihrms.aihrmsbackend.recruitment.controller;

import com.aihrms.aihrmsbackend.dto.response.CustomResponse;
import com.aihrms.aihrmsbackend.recruitment.dto.request.GenerateQuestionsRequest;
import com.aihrms.aihrmsbackend.recruitment.service.ResumeScreeningService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Skill-based interview questions for a screened resume.
 */
@RestController
@RequestMapping("/recruitment/interview-questions")
public class InterviewQuestionController {

    @Autowired
    private ResumeScreeningService resumeScreeningService;

    // POST /recruitment/interview-questions/generate
    @PostMapping("/generate")
    public ResponseEntity<?> generate(@RequestBody GenerateQuestionsRequest request) {
        CustomResponse response = resumeScreeningService.generateInterviewQuestions(request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    // GET /recruitment/interview-questions?resumeId=1
    @GetMapping
    public ResponseEntity<?> listByResume(@RequestParam("resumeId") Long resumeId) {
        CustomResponse response = resumeScreeningService.listInterviewQuestions(resumeId);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}