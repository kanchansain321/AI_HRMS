package com.aihrms.aihrmsbackend.entity;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name="resume")
public class EmpResume {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "candidate_name")
    private String candidateName;

    @Column(name = "email", length = 100)
    private String email;
    @Column(name = "filePath", length = 200)
    private String filePath;
    @Column(name = "upload_at", updatable = false)
    private Date uploadedAt;

    public EmpResume() {

    }

    @PrePersist
    protected void onCreate() {

        this.uploadedAt = new Date();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCandidateName() {
        return candidateName;
    }

    public void setCandidateName(String candidateName) {
        this.candidateName = candidateName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Date getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(Date uploadedAt) {
        this.uploadedAt = uploadedAt;
    }
}
