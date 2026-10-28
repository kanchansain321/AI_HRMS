package com.aihrms.aihrmsbackend.recruitment.client;

import com.aihrms.aihrmsbackend.ai.dto.CandidateScoreResult;
import com.aihrms.aihrmsbackend.ai.dto.GeneratedQuestion;
import com.aihrms.aihrmsbackend.ai.prompt.PromptBuilder;
import com.aihrms.aihrmsbackend.ai.service.CandidateScoringService;
import com.aihrms.aihrmsbackend.ai.service.GeminiService;
import com.aihrms.aihrmsbackend.ai.service.GeminiTask;
import com.aihrms.aihrmsbackend.ai.service.QuestionGenerationService;
import com.aihrms.aihrmsbackend.ai.service.SkillExtractionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AiServiceClientImpl implements AiServiceClient {

    private static final Logger log = LoggerFactory.getLogger(AiServiceClientImpl.class);

    private static final String[] LOCAL_TEMPLATES = {
            "Walk through how you would design a reliable feature that depends heavily on %s. What trade-offs would you make?",
            "A production issue appears related to %s under peak load. How would you diagnose root cause and mitigate impact?",
            "Compare two approaches involving %s and explain when you would choose each.",
            "What failure modes have you seen with %s, and how do you prevent them in code review or testing?",
            "Explain a non-obvious performance or security concern when using %s in a multi-service system."
    };

    private final SkillExtractionService skillExtractionService;
    private final QuestionGenerationService questionGenerationService;
    private final CandidateScoringService candidateScoringService;
    private final GeminiService geminiService;
    private final PromptBuilder promptBuilder;

    public AiServiceClientImpl(SkillExtractionService skillExtractionService,
                               QuestionGenerationService questionGenerationService,
                               CandidateScoringService candidateScoringService,
                               GeminiService geminiService,
                               PromptBuilder promptBuilder) {
        this.skillExtractionService = skillExtractionService;
        this.questionGenerationService = questionGenerationService;
        this.candidateScoringService = candidateScoringService;
        this.geminiService = geminiService;
        this.promptBuilder = promptBuilder;
    }

    @Override
    public List<String> extractSkills(String resumeText) {
        if (!geminiService.isConfigured()) {
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
    public List<String> extractSkillsFromFile(String filePath) {
        if (!geminiService.isConfigured()) {
            return List.of();
        }
        try {
            return skillExtractionService.extractSkillsFromFile(filePath);
        } catch (Exception e) {
            log.warn("Gemini extract-skills-from-file failed: {}", e.getMessage());
            return List.of();
        }
    }

    @Override
    public List<GeneratedQuestion> generateQuestions(List<String> skills, Integer count) {
        if (skills == null || skills.isEmpty()) {
            return List.of();
        }
        if (!geminiService.isConfigured()) {
            log.warn("Gemini not configured — using local interview question templates");
            return localGenerateQuestions(skills, count);
        }
        try {
            List<GeneratedQuestion> generated = questionGenerationService.generateQuestions(skills, count);
            if (generated != null && !generated.isEmpty()) {
                return generated;
            }
            log.warn("Gemini returned empty questions — using local templates");
        } catch (Exception e) {
            log.warn("Gemini generate-questions failed, using local templates: {}", e.getMessage());
        }
        return localGenerateQuestions(skills, count);
    }

    @Override
    public CandidateScoreResult scoreCandidate(String candidateName, List<String> requiredSkills, List<String> candidateSkills) {
        return candidateScoringService.score(candidateName, requiredSkills, candidateSkills);
    }

    @Override
    public String chat(String message) {
        if (!geminiService.isConfigured()) {
            throw new IllegalStateException(
                    "Gemini is not configured. Set GOOGLE_API_KEY (or gemini.api-key) to enable the chatbot.");
        }
        try {
            String reply = geminiService.ask(
                    GeminiTask.CHAT,
                    "You are an HR assistant for an HRMS product. Answer helpfully and briefly.\nUser: " + message);
            if (reply == null || reply.isBlank()) {
                throw new IllegalStateException("Gemini returned an empty chat reply");
            }
            return reply;
        } catch (IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalStateException("Error talking to Gemini: " + e.getMessage(), e);
        }
    }

    @Override
    public String summarizePerformance(String reviewPeriod, String ratingHint, String notes) {
        if (!geminiService.isConfigured()) {
            return "Performance notes recorded. AI summary unavailable (Gemini not configured).";
        }
        try {
            return geminiService.ask(
                    GeminiTask.SUMMARY,
                    promptBuilder.buildPerformanceSummaryPrompt(reviewPeriod, ratingHint, notes));
        } catch (Exception e) {
            return "Could not generate AI summary: " + e.getMessage();
        }
    }

    private List<GeneratedQuestion> localGenerateQuestions(List<String> skills, Integer count) {
        int limit = count != null && count > 0 ? count : skills.size();
        List<GeneratedQuestion> questions = new ArrayList<>();
        for (int i = 0; i < skills.size() && questions.size() < limit; i++) {
            String skill = skills.get(i);
            String template = LOCAL_TEMPLATES[i % LOCAL_TEMPLATES.length];
            questions.add(new GeneratedQuestion(skill, template.formatted(skill)));
        }
        return questions;
    }
}