package com.aihrms.aihrmsbackend.recruitment.service;

import com.aihrms.aihrmsbackend.dto.response.CustomResponse;
import com.aihrms.aihrmsbackend.recruitment.dto.request.PerformanceRequest;

public interface PerformanceService {
    CustomResponse create(PerformanceRequest request);
    CustomResponse listByEmployee(Long employeeId);
    CustomResponse listAll();
}