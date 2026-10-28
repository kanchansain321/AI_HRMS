package com.aihrms.aihrmsbackend.ai.service;

import com.aihrms.aihrmsbackend.ai.prompt.PromptBuilder;
import com.aihrms.aihrmsbackend.ai.util.ResumeTextExtractor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SkillExtractionService {

    private static final Logger log = LoggerFactory.getLogger(SkillExtractionService.class);

    private final GeminiService geminiService;
    private final PromptBuilder promptBuilder;
    private final ResumeTextExtractor resumeTextExtractor;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public SkillExtractionService(
            GeminiService geminiService,
            PromptBuilder promptBuilder,
            ResumeTextExtractor resumeTextExtractor) {
        this.geminiService = geminiService;
        this.promptBuilder = promptBuilder;
        this.resumeTextExtractor = resumeTextExtractor;
    }

    public List<String> extractSkills(String resumeText) {
        if (resumeText == null || resumeText.isBlank()) {
            return List.of();
        }
        String clipped = clip(resumeText);
        long start = System.currentTimeMillis();
        String raw = geminiService.ask(GeminiTask.SKILL, promptBuilder.buildSkillExtractionPrompt(clipped));
        List<String> skills = parseStringList(raw);
        log.info("Skill extraction done ms={} skills={} inChars={}",
                System.currentTimeMillis() - start, skills.size(), clipped.length());
        return skills;
    }

    public List<String> extractSkillsFromFile(String filePath) {
        try {
            String text = resumeTextExtractor.extractFromPath(filePath);
            if (text != null && !text.isBlank()) {
                return extractSkills(text);
            }
        } catch (Exception e) {
            log.warn("Local resume text extract failed, falling back to multimodal: {}", e.getMessage());
        }
        String prompt = promptBuilder.buildSkillExtractionPrompt(
                "Extract skills from the attached resume. Return ONLY a JSON array of skill strings.");
        String mime = resumeTextExtractor.guessMimeType(filePath);
        String raw = geminiService.askWithFilePath(prompt, filePath, mime);
        return parseStringList(raw);
    }

    String clip(String resumeText) {
        int maxChars = geminiService.props().getSkillMaxChars();
        String normalized = resumeText.replaceAll("[ \\t]+", " ").trim();
        if (normalized.length() <= maxChars) {
            return normalized;
        }
        return normalized.substring(0, maxChars);
    }

    private List<String> parseStringList(String raw) {
        try {
            String json = stripCodeFence(raw);
            List<String> list = objectMapper.readValue(json, new TypeReference<List<String>>() {});
            return list != null
                    ? list.stream().filter(s -> s != null && !s.isBlank()).map(String::trim).distinct().toList()
                    : List.of();
        } catch (Exception e) {
            throw new IllegalStateException(
                    "Failed to parse Gemini skills JSON: " + e.getMessage() + " | raw=" + raw, e);
        }
    }

    public static String stripCodeFence(String raw) {
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