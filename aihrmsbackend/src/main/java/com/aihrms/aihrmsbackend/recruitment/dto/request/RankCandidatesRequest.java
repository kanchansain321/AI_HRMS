///////////////request/////////////////////

package com.aihrms.aihrmsbackend.recruitment.dto.request;

import java.util.List;

public class RankCandidatesRequest {

    /** Required skills for the open role — used to score each resume. */
    private List<String> requiredSkills;

    public List<String> getRequiredSkills() {
        return requiredSkills;
    }

    public void setRequiredSkills(List<String> requiredSkills) {
        this.requiredSkills = requiredSkills;
    }
}