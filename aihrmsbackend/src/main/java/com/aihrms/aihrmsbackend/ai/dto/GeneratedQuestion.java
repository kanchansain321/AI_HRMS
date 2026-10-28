package com.aihrms.aihrmsbackend.ai.dto;

public class GeneratedQuestion {

    private String skill;
    private String question;

    public GeneratedQuestion() {
    }

    public GeneratedQuestion(String skill, String question) {
        this.skill = skill;
        this.question = question;
    }

    public String getSkill() {
        return skill;
    }

    public void setSkill(String skill) {
        this.skill = skill;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }
}