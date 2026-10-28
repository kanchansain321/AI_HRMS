package com.aihrms.aihrmsbackend.recruitment.service.impl;

import com.aihrms.aihrmsbackend.dto.response.CustomResponse;
import com.aihrms.aihrmsbackend.entity.Employee;
import com.aihrms.aihrmsbackend.exception.ResourceNotFoundException;
import com.aihrms.aihrmsbackend.recruitment.client.AiServiceClient;
import com.aihrms.aihrmsbackend.recruitment.dto.request.PerformanceRequest;
import com.aihrms.aihrmsbackend.recruitment.entity.Performance;
import com.aihrms.aihrmsbackend.recruitment.repository.PerformanceRepo;
import com.aihrms.aihrmsbackend.recruitment.service.PerformanceService;
import com.aihrms.aihrmsbackend.repository.EmployeeRepo;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PerformanceServiceImpl implements PerformanceService {

    private final PerformanceRepo performanceRepo;
    private final EmployeeRepo employeeRepo;
    private final AiServiceClient aiServiceClient;

    public PerformanceServiceImpl(PerformanceRepo performanceRepo, EmployeeRepo employeeRepo, AiServiceClient aiServiceClient) {
        this.performanceRepo = performanceRepo;
        this.employeeRepo = employeeRepo;
        this.aiServiceClient = aiServiceClient;
    }

    @Override
    @Transactional
    public CustomResponse create(PerformanceRequest request) {
        if (request == null || request.getEmployeeId() == null) {
            return new CustomResponse(HttpStatus.BAD_REQUEST.value(), null, "employeeId is required");
        }
        if (request.getReviewPeriod() == null || request.getReviewPeriod().isBlank()) {
            return new CustomResponse(HttpStatus.BAD_REQUEST.value(), null, "reviewPeriod is required");
        }
        Employee employee = employeeRepo.findById(request.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + request.getEmployeeId()));
        String ratingHint = request.getRating() != null ? request.getRating().toPlainString() : null;
        String summary = aiServiceClient.summarizePerformance(request.getReviewPeriod(), ratingHint, request.getNotes());
        Performance p = new Performance();
        p.setEmployee(employee);
        p.setReviewPeriod(request.getReviewPeriod().trim());
        p.setRating(request.getRating());
        p.setAiSummary(summary);
        return new CustomResponse(HttpStatus.CREATED.value(), toMap(performanceRepo.save(p)), "Performance review created");
    }

    @Override
    public CustomResponse listByEmployee(Long employeeId) {
        if (!employeeRepo.existsById(employeeId)) {
            throw new ResourceNotFoundException("Employee not found with id: " + employeeId);
        }
        List<Map<String, Object>> list = performanceRepo.findByEmployeeId(employeeId).stream().map(this::toMap).collect(Collectors.toList());
        CustomResponse response = new CustomResponse(HttpStatus.OK.value(), list, "Performance records fetched");
        response.setSize(list.size());
        return response;
    }

    @Override
    public CustomResponse listAll() {
        List<Map<String, Object>> list = performanceRepo.findAll().stream().map(this::toMap).collect(Collectors.toList());
        CustomResponse response = new CustomResponse(HttpStatus.OK.value(), list, "Performance records fetched");
        response.setSize(list.size());
        return response;
    }

    private Map<String, Object> toMap(Performance p) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", p.getId());
        m.put("employeeId", p.getEmployee() != null ? p.getEmployee().getId() : null);
        m.put("reviewPeriod", p.getReviewPeriod());
        m.put("rating", p.getRating());
        m.put("aiSummary", p.getAiSummary());
        return m;
    }
}