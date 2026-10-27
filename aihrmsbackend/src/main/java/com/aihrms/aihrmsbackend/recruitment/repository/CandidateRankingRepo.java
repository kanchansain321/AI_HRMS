////////////////////////////////repo////////////////////////////////

package com.aihrms.aihrmsbackend.recruitment.repository;

import com.aihrms.aihrmsbackend.recruitment.entity.CandidateRanking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CandidateRankingRepo extends JpaRepository<CandidateRanking, Long> {

    List<CandidateRanking> findAllByOrderByRankPositionAsc();

    Optional<CandidateRanking> findByResumeId(Long resumeId);

    void deleteAllInBatch();
}