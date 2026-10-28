package com.aihrms.aihrmsbackend.recruitment.dto.request;

public class CandidateAnswerRequest {
    private Long questionId;
    private String answerText;

    public Long getQuestionId() { return questionId; }
    public void setQuestionId(Long questionId) { this.questionId = questionId; }
    public String getAnswerText() { return answerText; }
    public void setAnswerText(String answerText) { this.answerText = answerText; }
}