package com.aihrms.aihrmsbackend.ai.dto;

import java.math.BigDecimal;

public class CandidateScoreResult {

    private BigDecimal score;
    private String feedback;

    public CandidateScoreResult() {
    }

    public CandidateScoreResult(BigDecimal score, String feedback) {
        this.score = score;
        this.feedback = feedback;
    }

    public BigDecimal getScore() {
        return score;
    }

    public void setScore(BigDecimal score) {
        this.score = score;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }
}