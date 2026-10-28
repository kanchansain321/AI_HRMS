package com.aihrms.aihrmsbackend.recruitment.client;

import com.aihrms.aihrmsbackend.ai.dto.CandidateScoreResult;
import com.aihrms.aihrmsbackend.ai.dto.GeneratedQuestion;

import java.util.List;

public interface AiServiceClient {

    List<String> extractSkills(String resumeText);

    List<String> extractSkillsFromFile(String filePath);

    List<GeneratedQuestion> generateQuestions(List<String> skills, Integer count);

    CandidateScoreResult scoreCandidate(String candidateName, List<String> requiredSkills, List<String> candidateSkills);

    String chat(String message);

    String summarizePerformance(String reviewPeriod, String ratingHint, String notes);
}