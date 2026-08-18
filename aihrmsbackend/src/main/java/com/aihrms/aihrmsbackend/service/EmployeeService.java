package com.aihrms.aihrmsbackend.service;

import com.aihrms.aihrmsbackend.dto.request.EmployeeAddUpdateReq;
import com.aihrms.aihrmsbackend.dto.response.EmployeeResponse;

public interface EmployeeService {
    EmployeeResponse createEmployee(EmployeeAddUpdateReq request);
}
