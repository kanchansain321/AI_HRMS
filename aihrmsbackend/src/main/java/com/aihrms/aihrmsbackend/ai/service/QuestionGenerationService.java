package com.aihrms.aihrmsbackend.ai.service;

import com.aihrms.aihrmsbackend.ai.prompt.PromptBuilder;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionGenerationService {

    private final GeminiService geminiService;
    private final PromptBuilder promptBuilder;
    private final ObjectMapper objectMapper=new ObjectMapper();

    public QuestionGenerationService(GeminiService geminiService,
                                     PromptBuilder promptBuilder) {
        this.geminiService = geminiService;
        this.promptBuilder = promptBuilder;

    }

    public List<String> generateQuestions(List<String> skills, Integer count) {
        if (skills == null || skills.isEmpty()) {
            return List.of();
        }
        String raw = geminiService.askToGemini(promptBuilder.buildQuestionGenerationPrompt(skills, count));
        return parseStringList(raw);
    }

    private List<String> parseStringList(String raw) {
        try {
            String json = stripCodeFence(raw);
            List<String> list = objectMapper.readValue(json, new TypeReference<List<String>>() {});
            return list != null ? list.stream().filter(s -> s != null && !s.isBlank()).map(String::trim).toList() : List.of();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to parse Gemini questions JSON: " + e.getMessage() + " | raw=" + raw, e);
        }
    }

    private String stripCodeFence(String raw) {
        if (raw == null) {
            return "[]";
        }
        String trimmed = raw.trim();
        if (trimmed.startsWith("```")) {
            int firstNl = trimmed.indexOf('\n');
            int lastFence = trimmed.lastIndexOf("```");
            if (firstNl > 0 && lastFence > firstNl) {
                trimmed = trimmed.substring(firstNl + 1, lastFence).trim();
            }
        }
        return trimmed;
    }
}
