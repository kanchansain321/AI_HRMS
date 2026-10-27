package com.aihrms.aihrmsbackend.ai.prompt;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PromptBuilder {

    public String buildSkillExtractionPrompt(String resumeText) {
        return """
                You are an HR recruiting assistant.
                Extract professional skills from the resume text below.
                Return ONLY a JSON array of skill strings, no markdown, no commentary.
                Example: ["Java","Spring Boot","MySQL"]

                Resume:
                %s
                """.formatted(resumeText != null ? resumeText : "");
    }

    public String buildQuestionGenerationPrompt(List<String> skills, Integer count) {
        String skillList = skills == null ? "" : skills.stream()
                .filter(s -> s != null && !s.isBlank())
                .collect(Collectors.joining(", "));
        int n = count != null && count > 0 ? count : Math.max(skills != null ? skills.size() : 0, 3);

        return """
                You are an HR interview assistant.
                Given these candidate skills: %s
                Generate exactly %d technical interview questions tailored to those skills.
                Return ONLY a JSON array of question strings, no markdown, no commentary.
                Example: ["Explain X","How would you design Y?"]
                """.formatted(skillList, n);
    }
}
