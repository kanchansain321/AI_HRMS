package com.aihrms.aihrmsbackend.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "candidate_ranking")
public class CandidateRanking {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @JoinColumn(name="resume_id",nullable = false)
    @ManyToOne
    private EmpResume resume;
    @Column(name = "score",precision = 5,scale = 2)
    private BigDecimal score;
    @Column(name = "feedback",columnDefinition = "TEXT")
    private String feedback;
    @Column(name = "rank_position")
    private Integer rankPosition;

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

    public Integer getRankPosition() {
        return rankPosition;
    }

    public void setRankPosition(Integer rankPosition) {
        this.rankPosition = rankPosition;
    }
}
