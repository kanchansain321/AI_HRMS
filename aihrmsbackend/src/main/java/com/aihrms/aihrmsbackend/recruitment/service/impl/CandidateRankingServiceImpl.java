///////////////////////////////////////impl////////////////////////////////////////////


package com.aihrms.aihrmsbackend.recruitment.service.impl;

import com.aihrms.aihrmsbackend.dto.response.CustomResponse;
import com.aihrms.aihrmsbackend.recruitment.dto.request.RankCandidatesRequest;
import com.aihrms.aihrmsbackend.recruitment.dto.response.CandidateRankingResponse;
import com.aihrms.aihrmsbackend.recruitment.entity.CandidateRanking;
import com.aihrms.aihrmsbackend.recruitment.entity.EmpResume;
import com.aihrms.aihrmsbackend.recruitment.entity.EmpSkills;
import com.aihrms.aihrmsbackend.recruitment.repository.CandidateRankingRepo;
import com.aihrms.aihrmsbackend.recruitment.repository.EmpResumeRepo;
import com.aihrms.aihrmsbackend.recruitment.repository.EmpSkillsRepo;
import com.aihrms.aihrmsbackend.recruitment.service.CandidateRankingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class CandidateRankingServiceImpl implements CandidateRankingService {

    @Autowired
    private EmpResumeRepo empResumeRepo;

    @Autowired
    private EmpSkillsRepo empSkillsRepo;

    @Autowired
    private CandidateRankingRepo candidateRankingRepo;

    @Override
    @Transactional
    public CustomResponse rankCandidates(RankCandidatesRequest request) {
        try {
            if (request == null || request.getRequiredSkills() == null || request.getRequiredSkills().isEmpty()) {
                return new CustomResponse(
                        HttpStatus.BAD_REQUEST.value(),
                        null,
                        "requiredSkills is required"
                );
            }

            List<String> required = request.getRequiredSkills().stream()
                    .filter(s -> s != null && !s.isBlank())
                    .map(s -> s.trim().toLowerCase(Locale.ROOT))
                    .collect(Collectors.toList());

            if (required.isEmpty()) {
                return new CustomResponse(HttpStatus.BAD_REQUEST.value(), null, "requiredSkills is required");
            }

            List<EmpResume> resumes = empResumeRepo.findAll();
            if (resumes.isEmpty()) {
                return new CustomResponse(HttpStatus.OK.value(), List.of(), "No resumes to rank");
            }

            candidateRankingRepo.deleteAllInBatch();

            List<CandidateRanking> rankings = new ArrayList<>();
            for (EmpResume resume : resumes) {
                List<String> candidateSkills = empSkillsRepo.findByResumeId(resume.getId()).stream()
                        .map(EmpSkills::getSkillName)
                        .filter(s -> s != null)
                        .map(s -> s.trim().toLowerCase(Locale.ROOT))
                        .collect(Collectors.toList());

                long matched = required.stream().filter(candidateSkills::contains).count();
                BigDecimal score = BigDecimal.valueOf(matched)
                        .multiply(BigDecimal.valueOf(100))
                        .divide(BigDecimal.valueOf(required.size()), 2, RoundingMode.HALF_UP);

                CandidateRanking ranking = new CandidateRanking();
                ranking.setResume(resume);
                ranking.setScore(score);
                ranking.setFeedback(matched + " of " + required.size() + " required skills matched");
                rankings.add(ranking);
            }

            rankings.sort(Comparator.comparing(CandidateRanking::getScore).reversed());
            int position = 1;
            for (CandidateRanking ranking : rankings) {
                ranking.setRankPosition(position++);
            }
            candidateRankingRepo.saveAll(rankings);

            List<CandidateRankingResponse> body = rankings.stream()
                    .map(this::toResponse)
                    .collect(Collectors.toList());

            return new CustomResponse(HttpStatus.OK.value(), body, "Candidates ranked successfully");
        } catch (Exception e) {
            return new CustomResponse(HttpStatus.BAD_REQUEST.value(), null, e.getMessage());
        }
    }

    @Override
    public CustomResponse getRankings() {
        List<CandidateRankingResponse> list = candidateRankingRepo.findAllByOrderByRankPositionAsc().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        CustomResponse response = new CustomResponse(HttpStatus.OK.value(), list, "Rankings fetched successfully");
        response.setSize(list.size());
        return response;
    }

    private CandidateRankingResponse toResponse(CandidateRanking ranking) {
        CandidateRankingResponse r = new CandidateRankingResponse();
        r.setId(ranking.getId());
        EmpResume resume = ranking.getResume();
        if (resume != null) {
            r.setResumeId(resume.getId());
            r.setCandidateName(resume.getCandidateName());
            r.setEmail(resume.getEmail());
        }
        r.setScore(ranking.getScore());
        r.setFeedback(ranking.getFeedback());
        r.setRankPosition(ranking.getRankPosition());
        return r;
    }
}