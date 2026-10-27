////////////////////////////////////////////client////////////////////////////////////////////


package com.aihrms.aihrmsbackend.recruitment.client;

import com.aihrms.aihrmsbackend.ai.service.GeminiService;
import com.aihrms.aihrmsbackend.ai.service.QuestionGenerationService;
import com.aihrms.aihrmsbackend.ai.service.SkillExtractionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * In-process client from recruitment → ai module (Gemini cloud agent).
 * Same contract as a future Feign client against a standalone ai-service.
 */
@Component
public class AiServiceClientImpl implements AiServiceClient {

    private static final Logger log = LoggerFactory.getLogger(AiServiceClientImpl.class);

    private final SkillExtractionService skillExtractionService;
    private final QuestionGenerationService questionGenerationService;
    private final GeminiService geminiService;

    public AiServiceClientImpl(SkillExtractionService skillExtractionService,
                               QuestionGenerationService questionGenerationService,
                               GeminiService geminiService) {
        this.skillExtractionService = skillExtractionService;
        this.questionGenerationService = questionGenerationService;
        this.geminiService = geminiService;
    }

    @Override
    public List<String> extractSkills(String resumeText) {
        if (!geminiService.isConfigured()) {
            log.warn("Gemini not configured; cannot extract skills from text");
            return List.of();
        }
        try {
            return skillExtractionService.extractSkills(resumeText);
        } catch (Exception e) {
            log.warn("Gemini extract-skills failed: {}", e.getMessage());
            return List.of();
        }
    }

    @Override
    public List<String> generateQuestions(List<String> skills, Integer count) {
        if (skills == null || skills.isEmpty()) {
            return List.of();
        }
        if (!geminiService.isConfigured()) {
            return localGenerateQuestions(skills, count);
        }
        try {
            return questionGenerationService.generateQuestions(skills, count);
        } catch (Exception e) {
            log.warn("Gemini generate-questions failed, using local fallback: {}", e.getMessage());
            return localGenerateQuestions(skills, count);
        }
    }

    private List<String> localGenerateQuestions(List<String> skills, Integer count) {
        int limit = count != null && count > 0 ? count : skills.size();
        List<String> questions = new ArrayList<>();
        for (int i = 0; i < skills.size() && questions.size() < limit; i++) {
            questions.add("Describe a project where you used " + skills.get(i) + ".");
        }
        return questions;
    }
}