package com.aihrms.aihrmsbackend.service;

import com.aihrms.aihrmsbackend.dto.response.CustomResponse;

public interface DepartmentService {
    CustomResponse addDepartment(String name);
}
