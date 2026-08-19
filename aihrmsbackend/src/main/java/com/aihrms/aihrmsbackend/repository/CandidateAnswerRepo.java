package com.aihrms.aihrmsbackend.repository;

import com.aihrms.aihrmsbackend.entity.CandidateAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CandidateAnswerRepo  extends JpaRepository<CandidateAnswer,Long> {
}
