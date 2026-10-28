//////////////////////////////////////////controller///////////////////////////////////////////////////////

package com.aihrms.aihrmsbackend.recruitment.controller;

import com.aihrms.aihrmsbackend.dto.response.CustomResponse;
import com.aihrms.aihrmsbackend.recruitment.dto.request.RankCandidatesRequest;
import com.aihrms.aihrmsbackend.recruitment.service.CandidateRankingService;
import com.aihrms.aihrmsbackend.recruitment.service.ResumeScreeningService;
import com.aihrms.aihrmsbackend.security.RequireAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Recruitment module — resume upload, list, screen, and candidate ranking.
 * Paths mirror the dedicated recruitment-service API under /recruitment.
 */
@RestController
@RequireAdmin
@RequestMapping("/recruitment/resumes")
public class ResumeController {

    @Autowired
    private ResumeScreeningService resumeScreeningService;

    @Autowired
    private CandidateRankingService candidateRankingService;

    // POST /recruitment/resumes/upload
    @PostMapping("/upload")
    public ResponseEntity<?> upload(
            @RequestParam("candidateName") String candidateName,
            @RequestParam("email") String email,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "skills", required = false) List<String> skills) {
        CustomResponse response = resumeScreeningService.uploadResume(candidateName, email, file, skills);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    // GET /recruitment/resumes
    @GetMapping
    public ResponseEntity<?> list() {
        CustomResponse response = resumeScreeningService.listResumes();
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    // GET /recruitment/resumes/rankings  (literal path before /{id})
    @GetMapping("/rankings")
    public ResponseEntity<?> rankings() {
        CustomResponse response = candidateRankingService.getRankings();
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    // POST /recruitment/resumes/rank
    @PostMapping("/rank")
    public ResponseEntity<?> rank(@RequestBody RankCandidatesRequest request) {
        CustomResponse response = candidateRankingService.rankCandidates(request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    // GET /recruitment/resumes/{id}
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        CustomResponse response = resumeScreeningService.getResume(id);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    // POST /recruitment/resumes/{id}/screen
    @PostMapping("/{id}/screen")
    public ResponseEntity<?> screen(
            @PathVariable Long id,
            @RequestParam(value = "skills", required = false) List<String> skills,
            @RequestParam(value = "resumeText", required = false) String resumeText) {
        CustomResponse response = resumeScreeningService.screenResume(id, skills, resumeText);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}