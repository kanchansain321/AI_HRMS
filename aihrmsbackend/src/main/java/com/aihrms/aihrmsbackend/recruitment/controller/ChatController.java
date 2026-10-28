package com.aihrms.aihrmsbackend.recruitment.controller;

import com.aihrms.aihrmsbackend.dto.response.CustomResponse;
import com.aihrms.aihrmsbackend.recruitment.dto.request.ChatRequest;
import com.aihrms.aihrmsbackend.recruitment.service.ChatHistoryService;
import com.aihrms.aihrmsbackend.security.RequireAdmin;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/recruitment/chat")
@RequireAdmin
public class ChatController {

    private final ChatHistoryService chatHistoryService;

    public ChatController(ChatHistoryService chatHistoryService) {
        this.chatHistoryService = chatHistoryService;
    }

    @PostMapping
    public ResponseEntity<?> chat(@RequestBody ChatRequest request) {
        CustomResponse response = chatHistoryService.chat(request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/history/{employeeId}")
    public ResponseEntity<?> history(@PathVariable Long employeeId) {
        CustomResponse response = chatHistoryService.historyByEmployee(employeeId);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}