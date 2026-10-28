package com.aihrms.aihrmsbackend.recruitment.repository;

import com.aihrms.aihrmsbackend.recruitment.entity.CandidateAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CandidateAnswerRepo extends JpaRepository<CandidateAnswer, Long> {
    List<CandidateAnswer> findByQuestionId(Long questionId);
    List<CandidateAnswer> findByQuestionResumeId(Long resumeId);
}