package com.aihrms.aihrmsbackend.repository;

import com.aihrms.aihrmsbackend.entity.CandidateRanking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CandidateRankingRepo extends JpaRepository<CandidateRanking,Long> {
}
