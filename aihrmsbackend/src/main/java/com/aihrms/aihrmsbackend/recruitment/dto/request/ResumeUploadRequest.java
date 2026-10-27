//////////////////////request//////////////////////////

package com.aihrms.aihrmsbackend.recruitment.dto.request;

import java.util.List;

/**
 * Metadata for resume upload. The resume file itself is sent as multipart
 * alongside this data (candidateName + email as form fields, or this JSON part).
 */
public class ResumeUploadRequest {

    private String candidateName;
    private String email;
    /** Optional skills when AI extraction is not available. */
    private List<String> skills;

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

    public List<String> getSkills() {
        return skills;
    }

    public void setSkills(List<String> skills) {
        this.skills = skills;
    }
}