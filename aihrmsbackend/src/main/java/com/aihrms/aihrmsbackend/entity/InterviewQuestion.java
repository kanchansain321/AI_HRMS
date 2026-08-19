package com.aihrms.aihrmsbackend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "interview_quetions")
public class InterviewQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private  Long id;
    @JoinColumn(name = "resume_id",nullable = false)
    @ManyToOne
    private  EmpResume resume;
    @Column(name = "question_text",columnDefinition = "TEXT")
    private String questionText;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EmpResume getResume() {
        return resume;
    }

    public void setResume(EmpResume resume) {
        this.resume = resume;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }
}
