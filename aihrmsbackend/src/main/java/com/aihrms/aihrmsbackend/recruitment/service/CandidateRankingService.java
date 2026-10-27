///////////////////////////////////////////////////service///////////////////////////////////////////


package com.aihrms.aihrmsbackend.recruitment.service;

import com.aihrms.aihrmsbackend.dto.response.CustomResponse;
import com.aihrms.aihrmsbackend.recruitment.dto.request.RankCandidatesRequest;

public interface CandidateRankingService {

    /** Score all resumes against required skills and persist rank positions. */
    CustomResponse rankCandidates(RankCandidatesRequest request);

    CustomResponse getRankings();
}