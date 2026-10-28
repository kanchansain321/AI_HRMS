package com.aihrms.aihrmsbackend.recruitment.dto.request;

import java.math.BigDecimal;

public class PerformanceRequest {
    private Long employeeId;
    private String reviewPeriod;
    private BigDecimal rating;
    private String notes;

    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }
    public String getReviewPeriod() { return reviewPeriod; }
    public void setReviewPeriod(String reviewPeriod) { this.reviewPeriod = reviewPeriod; }
    public BigDecimal getRating() { return rating; }
    public void setRating(BigDecimal rating) { this.rating = rating; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}