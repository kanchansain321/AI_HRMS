package com.aihrms.aihrmsbackend.recruitment.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "interview_questions")
public class InterviewQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @JoinColumn(name = "resume_id", nullable = false)
    @ManyToOne
    private EmpResume resume;

    @Column(name = "skill_name", length = 100)
    private String skillName;

    @Column(name = "question_text", columnDefinition = "TEXT")
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

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }
}