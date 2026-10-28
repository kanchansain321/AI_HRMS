package com.aihrms.aihrmsbackend.recruitment.service.impl;

import com.aihrms.aihrmsbackend.dto.response.CustomResponse;
import com.aihrms.aihrmsbackend.entity.Employee;
import com.aihrms.aihrmsbackend.exception.ResourceNotFoundException;
import com.aihrms.aihrmsbackend.recruitment.client.AiServiceClient;
import com.aihrms.aihrmsbackend.recruitment.dto.request.ChatRequest;
import com.aihrms.aihrmsbackend.recruitment.entity.ChatHistory;
import com.aihrms.aihrmsbackend.recruitment.repository.ChatHistoryRepo;
import com.aihrms.aihrmsbackend.recruitment.service.ChatHistoryService;
import com.aihrms.aihrmsbackend.repository.EmployeeRepo;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ChatHistoryServiceImpl implements ChatHistoryService {

    private final ChatHistoryRepo chatHistoryRepo;
    private final EmployeeRepo employeeRepo;
    private final AiServiceClient aiServiceClient;

    public ChatHistoryServiceImpl(ChatHistoryRepo chatHistoryRepo, EmployeeRepo employeeRepo, AiServiceClient aiServiceClient) {
        this.chatHistoryRepo = chatHistoryRepo;
        this.employeeRepo = employeeRepo;
        this.aiServiceClient = aiServiceClient;
    }

    @Override
    @Transactional
    public CustomResponse chat(ChatRequest request) {
        if (request == null || request.getMessage() == null || request.getMessage().isBlank()) {
            return new CustomResponse(HttpStatus.BAD_REQUEST.value(), null, "message is required");
        }
        Employee employee = null;
        if (request.getEmployeeId() != null) {
            employee = employeeRepo.findById(request.getEmployeeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + request.getEmployeeId()));
        }
        String reply = aiServiceClient.chat(request.getMessage().trim());
        ChatHistory history = new ChatHistory();
        history.setEmployee(employee);
        history.setMessage(request.getMessage().trim());
        history.setResponse(reply);
        return new CustomResponse(HttpStatus.CREATED.value(), toMap(chatHistoryRepo.save(history)), "Chat response generated");
    }

    @Override
    public CustomResponse historyByEmployee(Long employeeId) {
        if (!employeeRepo.existsById(employeeId)) {
            throw new ResourceNotFoundException("Employee not found with id: " + employeeId);
        }
        List<Map<String, Object>> list = chatHistoryRepo.findByEmployeeIdOrderByCreatedAtDesc(employeeId).stream()
                .map(this::toMap).collect(Collectors.toList());
        CustomResponse response = new CustomResponse(HttpStatus.OK.value(), list, "Chat history fetched");
        response.setSize(list.size());
        return response;
    }

    private Map<String, Object> toMap(ChatHistory h) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", h.getId());
        m.put("employeeId", h.getEmployee() != null ? h.getEmployee().getId() : null);
        m.put("message", h.getMessage());
        m.put("response", h.getResponse());
        m.put("createdAt", h.getCreatedAt());
        return m;
    }
}