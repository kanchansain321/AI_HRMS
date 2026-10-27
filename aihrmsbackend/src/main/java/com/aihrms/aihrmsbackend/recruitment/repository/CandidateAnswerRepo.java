package com.aihrms.aihrmsbackend.recruitment.repository;

import com.aihrms.aihrmsbackend.recruitment.entity.CandidateAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CandidateAnswerRepo  extends JpaRepository<CandidateAnswer,Long> {
}
