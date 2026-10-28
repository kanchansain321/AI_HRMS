package com.aihrms.aihrmsbackend.ai.prompt;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PromptBuilder {

    public String buildSkillExtractionPrompt(String resumeText) {
        return """
                Extract technical and professional skills from this resume.
                Return a JSON array of short skill strings only.
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
                You are a senior technical interviewer hiring for a mid-to-senior engineering role.
                Candidate skills: %s

                Generate exactly %d distinct interview questions.
                Mix question types across the set:
                - deep technical / how-it-works
                - debugging or failure scenario
                - design / trade-off
                - practical experience (without being generic)

                Rules:
                - One skill focus per question; cover different skills when possible.
                - Each question must be specific and answerable in 1–3 minutes.
                - Do NOT use templates like "Describe a project where you used X" or "Tell me about your experience with X".
                - Do NOT repeat the same structure for every skill.

                Return ONLY a JSON array of objects with "skill" and "question".
                Example: [{"skill":"Java","question":"How would you diagnose and fix a memory leak in a long-running JVM service?"}]
                """.formatted(skillList, n);
    }

    public String buildRankingPrompt(String candidateName, List<String> requiredSkills, List<String> candidateSkills) {
        return """
                You are an HR recruiting assistant scoring a candidate for a role.
                Candidate: %s
                Required skills: %s
                Candidate skills: %s
                Return ONLY JSON: {"score": <0-100 number>, "feedback": "<short reason>"}
                No markdown, no commentary.
                """.formatted(
                candidateName != null ? candidateName : "unknown",
                requiredSkills == null ? "" : String.join(", ", requiredSkills),
                candidateSkills == null ? "" : String.join(", ", candidateSkills)
        );
    }

    public String buildPerformanceSummaryPrompt(String reviewPeriod, String ratingHint, String notes) {
        return """
                You are an HR performance coach.
                Review period: %s
                Rating hint: %s
                Notes: %s
                Write a concise AI performance summary (3-5 sentences). Return plain text only.
                """.formatted(
                reviewPeriod != null ? reviewPeriod : "",
                ratingHint != null ? ratingHint : "",
                notes != null ? notes : ""
        );
    }
}