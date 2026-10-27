package com.aihrms.aihrmsbackend.recruitment.repository;

import com.aihrms.aihrmsbackend.recruitment.entity.CandidateRanking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CandidateRankingRepo extends JpaRepository<CandidateRanking,Long> {
}
