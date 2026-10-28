package com.aihrms.aihrmsbackend.ai.service;

import com.aihrms.aihrmsbackend.ai.dto.CandidateScoreResult;
import com.aihrms.aihrmsbackend.ai.prompt.PromptBuilder;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Locale;

@Service
public class CandidateScoringService {

    private final GeminiService geminiService;
    private final PromptBuilder promptBuilder;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public CandidateScoringService(GeminiService geminiService, PromptBuilder promptBuilder) {
        this.geminiService = geminiService;
        this.promptBuilder = promptBuilder;
    }

    /**
     * Score a candidate. Uses local skill overlap by default (fast).
     * Set gemini.ranking.use-ai=true to call the RANK model instead.
     */
    public CandidateScoreResult score(String candidateName, List<String> requiredSkills, List<String> candidateSkills) {
        if (geminiService.isConfigured() && geminiService.props().isRankingUseAi()) {
            try {
                String raw = geminiService.ask(
                        GeminiTask.RANK,
                        promptBuilder.buildRankingPrompt(candidateName, requiredSkills, candidateSkills));
                String json = SkillExtractionService.stripCodeFence(raw);
                JsonNode node = objectMapper.readTree(json);
                BigDecimal score = node.has("score")
                        ? new BigDecimal(node.get("score").asText()).setScale(2, RoundingMode.HALF_UP)
                        : overlapScore(requiredSkills, candidateSkills);
                String feedback = node.hasNonNull("feedback") ? node.get("feedback").asText() : "Scored by Gemini";
                return new CandidateScoreResult(score, feedback);
            } catch (Exception ignored) {
                // fall through to local
            }
        }
        return scoreLocal(requiredSkills, candidateSkills);
    }

    public CandidateScoreResult scoreLocal(List<String> requiredSkills, List<String> candidateSkills) {
        BigDecimal score = overlapScore(requiredSkills, candidateSkills);
        long matched = matchedCount(requiredSkills, candidateSkills);
        return new CandidateScoreResult(
                score,
                matched + " of " + (requiredSkills == null ? 0 : requiredSkills.size()) + " required skills matched"
        );
    }

    private BigDecimal overlapScore(List<String> required, List<String> candidate) {
        if (required == null || required.isEmpty()) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        long matched = matchedCount(required, candidate);
        return BigDecimal.valueOf(matched)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(required.size()), 2, RoundingMode.HALF_UP);
    }

    private long matchedCount(List<String> required, List<String> candidate) {
        if (required == null || candidate == null) {
            return 0;
        }
        List<String> cand = candidate.stream()
                .filter(s -> s != null)
                .map(s -> s.trim().toLowerCase(Locale.ROOT))
                .toList();
        return required.stream()
                .filter(s -> s != null && !s.isBlank())
                .map(s -> s.trim().toLowerCase(Locale.ROOT))
                .filter(cand::contains)
                .count();
    }
}