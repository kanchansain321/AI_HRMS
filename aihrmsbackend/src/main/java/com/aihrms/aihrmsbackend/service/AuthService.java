package com.aihrms.aihrmsbackend.service;

import com.aihrms.aihrmsbackend.dto.request.LoginRequest;
import com.aihrms.aihrmsbackend.dto.response.CustomResponse;

public interface AuthService {

    CustomResponse login(LoginRequest request);

    CustomResponse me(Long employeeId);
}