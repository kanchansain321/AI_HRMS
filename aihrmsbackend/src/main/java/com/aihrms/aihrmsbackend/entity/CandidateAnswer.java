package com.aihrms.aihrmsbackend.entity;

import jakarta.persistence.*;
@Entity
@Table(name = "candidate_answers")

public class CandidateAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private  long id;
    @JoinColumn(name = "question_id",nullable = false)
    @ManyToOne
    private InterviewQuestion question;
    @Column(name = "answer_text",columnDefinition = "text")
    private String answerText;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public InterviewQuestion getQuestion() {
        return question;
    }

    public void setQuestion(InterviewQuestion question) {
        this.question = question;
    }

    public String getAnswerText() {
        return answerText;
    }

    public void setAnswerText(String answerText) {
        this.answerText = answerText;
    }
}
