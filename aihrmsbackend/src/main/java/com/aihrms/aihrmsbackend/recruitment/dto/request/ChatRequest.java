package com.aihrms.aihrmsbackend.recruitment.dto.request;

public class ChatRequest {
    private Long employeeId;
    private String message;

    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}