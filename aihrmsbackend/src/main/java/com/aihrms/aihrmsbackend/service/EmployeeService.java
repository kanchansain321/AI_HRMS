package com.aihrms.aihrmsbackend.service;

import com.aihrms.aihrmsbackend.dto.request.EmployeeAddUpdateReq;
import com.aihrms.aihrmsbackend.dto.response.EmployeeResponse;

import java.util.List;

public interface EmployeeService {
    EmployeeResponse createEmployee(EmployeeAddUpdateReq request);

    EmployeeResponse findByIdEmployee(Long id);

    //GetAll
    List<EmployeeResponse> findAll();
}
