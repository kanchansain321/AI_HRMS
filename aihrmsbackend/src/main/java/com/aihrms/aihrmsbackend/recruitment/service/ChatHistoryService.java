package com.aihrms.aihrmsbackend.recruitment.service;

import com.aihrms.aihrmsbackend.dto.response.CustomResponse;
import com.aihrms.aihrmsbackend.recruitment.dto.request.ChatRequest;

public interface ChatHistoryService {
    CustomResponse chat(ChatRequest request);
    CustomResponse historyByEmployee(Long employeeId);
}