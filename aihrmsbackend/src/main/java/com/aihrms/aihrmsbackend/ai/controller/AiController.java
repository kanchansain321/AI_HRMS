package com.aihrms.aihrmsbackend.ai.controller;

import com.aihrms.aihrmsbackend.ai.dto.GeneratedQuestion;
import com.aihrms.aihrmsbackend.ai.dto.request.ExtractSkillsRequest;
import com.aihrms.aihrmsbackend.ai.dto.request.GenerateQuestionsAiRequest;
import com.aihrms.aihrmsbackend.ai.service.GeminiService;
import com.aihrms.aihrmsbackend.ai.service.QuestionGenerationService;
import com.aihrms.aihrmsbackend.ai.service.SkillExtractionService;
import com.aihrms.aihrmsbackend.security.RequireAdmin;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin
@RequireAdmin
public class AiController {

    private final SkillExtractionService skillExtractionService;
    private final QuestionGenerationService questionGenerationService;
    private final GeminiService geminiService;

    public AiController(SkillExtractionService skillExtractionService,
                        QuestionGenerationService questionGenerationService,
                        GeminiService geminiService) {
        this.skillExtractionService = skillExtractionService;
        this.questionGenerationService = questionGenerationService;
        this.geminiService = geminiService;
    }

    @PostMapping("/extract-skills")
    public ResponseEntity<List<String>> extractSkills(@RequestBody ExtractSkillsRequest request) {
        List<String> skills = skillExtractionService.extractSkills(
                request != null ? request.getResumeText() : null);
        return ResponseEntity.ok(skills);
    }

    @PostMapping("/generate-questions")
    public ResponseEntity<List<GeneratedQuestion>> generateQuestions(@RequestBody GenerateQuestionsAiRequest request) {
        List<GeneratedQuestion> questions = questionGenerationService.generateQuestions(
                request != null ? request.getSkills() : null,
                request != null ? request.getCount() : null);
        return ResponseEntity.ok(questions);
    }

    @PostMapping(value = "/ask", produces = "text/event-stream")
    public SseEmitter askGemini(
            @RequestParam String inputType,
            @RequestParam(required = false) String text,
            @RequestParam(required = false) MultipartFile file) {

        SseEmitter emitter = new SseEmitter(0L);
        CompletableFuture.runAsync(() -> {
            try {
                geminiService.askToGeminiStream(text, file, inputType, line -> {
                    try {
                        emitter.send(SseEmitter.event().data(line));
                    } catch (IOException e) {
                        emitter.completeWithError(e);
                    }
                });
                emitter.complete();
            } catch (Exception e) {
                try {
                    emitter.send("Error: " + e.getMessage());
                } catch (Exception ignored) {
                }
                emitter.completeWithError(e);
            }
        });
        return emitter;
    }
}