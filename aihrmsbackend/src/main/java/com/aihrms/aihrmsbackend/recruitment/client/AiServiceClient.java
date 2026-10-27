////////////////////////////////////////////////clilent//////////////////////////////////////////


package com.aihrms.aihrmsbackend.recruitment.client;

import java.util.List;

/**
 * Client toward the AI capability (extract skills / generate interview questions).
 * In the monolith this is a local RestClient (or in-process) call; when split out as
 * microservices it maps 1:1 to a Feign client against {@code ai-service}.
 */
public interface AiServiceClient {

    /**
     * Extract skills from resume text via AI service.
     * POST /api/ai/extract-skills
     */
    List<String> extractSkills(String resumeText);

    /**
     * Generate skill-based interview questions via AI service.
     * POST /api/ai/generate-questions
     */
    List<String> generateQuestions(List<String> skills, Integer count);
}