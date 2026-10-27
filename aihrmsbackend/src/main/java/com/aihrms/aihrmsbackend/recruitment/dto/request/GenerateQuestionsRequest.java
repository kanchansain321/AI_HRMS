/////////////////request//////////////////

package com.aihrms.aihrmsbackend.recruitment.dto.request;

public class GenerateQuestionsRequest {

    private Long resumeId;
    /** How many questions to generate (optional; AI / local fallback decides if null). */
    private Integer count;

    public Long getResumeId() {
        return resumeId;
    }

    public void setResumeId(Long resumeId) {
        this.resumeId = resumeId;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}