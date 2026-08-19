package com.aihrms.aihrmsbackend.entity;

import jakarta.persistence.*;

import java.util.Date;
@Entity
@Table(name = "chat_history")

public class ChatHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private  long id;

    @JoinColumn(name = "employee_id")
    @ManyToOne
    private  Employee  employee;
    @Column(name = "message",columnDefinition = "TEXT")
    private String message;
    @Column(name = "response",columnDefinition = "TEXT")
     private String response;
    @Column(name = "created_at",updatable = false)
     private Date createdAt;
    @PrePersist
    protected void onCreate()
    {
        this.createdAt= new Date();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
