package com.aihrms.aihrmsbackend.ai.service;

import com.aihrms.aihrmsbackend.ai.dto.GeneratedQuestion;
import com.aihrms.aihrmsbackend.ai.prompt.PromptBuilder;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class QuestionGenerationService {

    private static final Logger log = LoggerFactory.getLogger(QuestionGenerationService.class);

    private final GeminiService geminiService;
    private final PromptBuilder promptBuilder;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public QuestionGenerationService(GeminiService geminiService, PromptBuilder promptBuilder) {
        this.geminiService = geminiService;
        this.promptBuilder = promptBuilder;
    }

    public List<GeneratedQuestion> generateQuestions(List<String> skills, Integer count) {
        if (skills == null || skills.isEmpty()) {
            return List.of();
        }
        int max = Math.max(1, geminiService.props().getQuestionsMax());
        List<String> limitedSkills = skills.stream()
                .filter(s -> s != null && !s.isBlank())
                .map(String::trim)
                .distinct()
                .limit(max)
                .toList();
        int n = count != null && count > 0 ? Math.min(count, max) : Math.min(Math.max(limitedSkills.size(), 3), max);

        long start = System.currentTimeMillis();
        String prompt = promptBuilder.buildQuestionGenerationPrompt(limitedSkills, n);
        List<GeneratedQuestion> out = parseQuestions(
                geminiService.ask(GeminiTask.QUESTIONS, prompt),
                limitedSkills);
        // Reject mostly-template batches so the client can fall back to varied locals.
        if (out.size() < Math.max(1, (n + 1) / 2)) {
            throw new IllegalStateException(
                    "Gemini returned too few usable interview questions (got " + out.size() + ", needed ~" + n + ")");
        }
        log.info("Question generation done ms={} questions={}", System.currentTimeMillis() - start, out.size());
        return out;
    }

    private List<GeneratedQuestion> parseQuestions(String raw, List<String> skills) {
        try {
            String json = SkillExtractionService.stripCodeFence(raw);
            JsonNode root = objectMapper.readTree(json);
            List<GeneratedQuestion> out = new ArrayList<>();
            if (root.isArray()) {
                for (int i = 0; i < root.size(); i++) {
                    JsonNode n = root.get(i);
                    if (n.isTextual()) {
                        String skill = i < skills.size() ? skills.get(i) : null;
                        String q = n.asText();
                        if (!isWeakQuestion(q)) {
                            out.add(new GeneratedQuestion(skill, q));
                        }
                    } else {
                        String skill = n.hasNonNull("skill") ? n.get("skill").asText()
                                : (i < skills.size() ? skills.get(i) : null);
                        String question = n.hasNonNull("question") ? n.get("question").asText() : null;
                        if (question != null && !question.isBlank() && !isWeakQuestion(question)) {
                            out.add(new GeneratedQuestion(skill, question.trim()));
                        }
                    }
                }
            }
            return out;
        } catch (Exception e) {
            throw new IllegalStateException(
                    "Failed to parse Gemini questions JSON: " + e.getMessage() + " | raw=" + raw, e);
        }
    }

    static boolean isWeakQuestion(String question) {
        if (question == null || question.isBlank()) {
            return true;
        }
        String q = question.toLowerCase(Locale.ROOT).replaceAll("\\s+", " ").trim();
        return q.contains("describe a project where you used")
                || q.contains("describe a project in which you used")
                || q.contains("describe one project where")
                || q.contains("tell me about your experience with")
                || q.contains("tell me about a time you used")
                || q.contains("what is your experience with")
                || q.contains("have you used") && q.contains("?") && q.length() < 60;
    }
}