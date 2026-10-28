package com.aihrms.aihrmsbackend.recruitment.controller;

import com.aihrms.aihrmsbackend.dto.response.CustomResponse;
import com.aihrms.aihrmsbackend.recruitment.dto.request.PerformanceRequest;
import com.aihrms.aihrmsbackend.recruitment.service.PerformanceService;
import com.aihrms.aihrmsbackend.security.RequireAdmin;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/recruitment/performance")
@RequireAdmin
public class PerformanceController {

    private final PerformanceService performanceService;

    public PerformanceController(PerformanceService performanceService) {
        this.performanceService = performanceService;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody PerformanceRequest request) {
        CustomResponse response = performanceService.create(request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping
    public ResponseEntity<?> listAll() {
        CustomResponse response = performanceService.listAll();
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<?> listByEmployee(@PathVariable Long employeeId) {
        CustomResponse response = performanceService.listByEmployee(employeeId);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}