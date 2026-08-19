package com.aihrms.aihrmsbackend.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "performance")
public class Performance {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private   Long id;
    @JoinColumn(name = "employee_id",nullable = false)
    @ManyToOne
    private Employee employee;
    @Column(name = "review_period",length = 20)
    private String reviewPeriod;
    @Column(name = "rating",length = 20)
    private BigDecimal rating;
    @Column(name = "ai_summary",precision = 3,scale = 2)
    private String aiSummary;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public String getReviewPeriod() {
        return reviewPeriod;
    }

    public void setReviewPeriod(String reviewPeriod) {
        this.reviewPeriod = reviewPeriod;
    }

    public BigDecimal getRating() {
        return rating;
    }

    public void setRating(BigDecimal rating) {
        this.rating = rating;
    }

    public String getAiSummary() {
        return aiSummary;
    }

    public void setAiSummary(String aiSummary) {
        this.aiSummary = aiSummary;
    }
}
