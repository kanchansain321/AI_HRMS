package com.aihrms.aihrmsbackend.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "performance")
public class Performance {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private  Integer id;
    @Column(name = "employee_id",nullable = false)
    private Integer employee;
    @Column(name = "review_period",length = 20)
    private String reviewPeriod;
    @Column(name = "rating",length = 20)
    private BigDecimal rating;
    @Column(name = "ai_summary",precision = 3,scale = 2)
    private String aiSummary;

}
