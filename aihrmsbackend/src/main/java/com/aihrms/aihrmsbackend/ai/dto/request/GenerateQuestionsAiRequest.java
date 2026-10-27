package com.aihrms.aihrmsbackend.ai.dto.request;

import java.util.List;

public class GenerateQuestionsAiRequest {

    private List<String> skills;
    private Integer count;

    public List<String> getSkills() {
        return skills;
    }

    public void setSkills(List<String> skills) {
        this.skills = skills;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
