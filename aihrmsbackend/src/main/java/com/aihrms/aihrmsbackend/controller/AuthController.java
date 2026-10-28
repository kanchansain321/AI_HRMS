package com.aihrms.aihrmsbackend.controller;

import com.aihrms.aihrmsbackend.dto.request.LoginRequest;
import com.aihrms.aihrmsbackend.dto.response.CustomResponse;
import com.aihrms.aihrmsbackend.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        CustomResponse response = authService.login(request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(HttpServletRequest request) {
        Object idAttr = request.getAttribute("employeeId");
        if (idAttr == null) {
            CustomResponse unauthorized = new CustomResponse(401, null, "Unauthorized");
            return ResponseEntity.status(401).body(unauthorized);
        }
        CustomResponse response = authService.me(Long.valueOf(idAttr.toString()));
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}