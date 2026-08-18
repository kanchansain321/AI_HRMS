package com.aihrms.aihrmsbackend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "interview_quetions")
public class InterviewQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private  Integer id;
    @Column(name = "resume_id",nullable = false)
    private  Integer resume;
    @Column(name = "question_text",columnDefinition = "TEXT")
    private String questionText;

}
