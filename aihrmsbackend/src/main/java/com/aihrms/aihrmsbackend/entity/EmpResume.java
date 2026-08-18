package com.aihrms.aihrmsbackend.entity;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name="resume")
public class EmpResume {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)

    private Integer id;
    private String candidateName;
    private String email;
    private String filePath;
    private Date uploadedAt;

   public EmpResume()
   {

   }
   @PrePersist
    protected  void onCreate()
   {
       this.uploadedAt=new Date();
   }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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
