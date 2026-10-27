//////////////////////////////////response///////////////////////////////


package com.aihrms.aihrmsbackend.recruitment.dto.response;

import java.util.List;

public class SkillExtractionResponse {

    private Long resumeId;
    private List<String> skills;

    public SkillExtractionResponse() {
    }

    public SkillExtractionResponse(Long resumeId, List<String> skills) {
        this.resumeId = resumeId;
        this.skills = skills;
    }

    public Long getResumeId() {
        return resumeId;
    }

    public void setResumeId(Long resumeId) {
        this.resumeId = resumeId;
    }

    public List<String> getSkills() {
        return skills;
    }

    public void setSkills(List<String> skills) {
        this.skills = skills;
    }
}