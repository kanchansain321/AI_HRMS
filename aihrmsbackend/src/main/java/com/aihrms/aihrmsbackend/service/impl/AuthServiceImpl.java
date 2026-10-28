package com.aihrms.aihrmsbackend.service.impl;

import com.aihrms.aihrmsbackend.dto.request.LoginRequest;
import com.aihrms.aihrmsbackend.dto.response.CustomResponse;
import com.aihrms.aihrmsbackend.dto.response.LoginResponse;
import com.aihrms.aihrmsbackend.entity.Employee;
import com.aihrms.aihrmsbackend.exception.ResourceNotFoundException;
import com.aihrms.aihrmsbackend.repository.EmployeeRepo;
import com.aihrms.aihrmsbackend.security.BootstrapAuth;
import com.aihrms.aihrmsbackend.security.JwtUtil;
import com.aihrms.aihrmsbackend.security.Roles;
import com.aihrms.aihrmsbackend.service.AuthService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class AuthServiceImpl implements AuthService {

    private final EmployeeRepo employeeRepo;
    private final JwtUtil jwtUtil;
    private final String bootstrapEmail;
    private final String bootstrapPassword;
    private final String bootstrapName;

    public AuthServiceImpl(
            EmployeeRepo employeeRepo,
            JwtUtil jwtUtil,
            @Value("${auth.bootstrap.email}") String bootstrapEmail,
            @Value("${auth.bootstrap.password}") String bootstrapPassword,
            @Value("${auth.bootstrap.name:Super Admin}") String bootstrapName) {
        this.employeeRepo = employeeRepo;
        this.jwtUtil = jwtUtil;
        this.bootstrapEmail = bootstrapEmail;
        this.bootstrapPassword = bootstrapPassword;
        this.bootstrapName = bootstrapName;
    }

    @Override
    public CustomResponse login(LoginRequest request) {
        if (request == null || request.getEmail() == null || request.getPassword() == null) {
            return new CustomResponse(HttpStatus.BAD_REQUEST.value(), null, "email and password are required");
        }

        String email = request.getEmail().trim();
        String password = request.getPassword();

        Employee employee = employeeRepo.findFirstByEmail(email).orElse(null);

        if (employee != null
                && !Boolean.FALSE.equals(employee.getActive())
                && employee.getPassword() != null
                && employee.getPassword().equals(password)) {
            String role = Roles.canonical(employee.getRole() != null ? employee.getRole().getName() : Roles.EMPLOYEE);
            String token = jwtUtil.generateToken(employee.getId(), employee.getEmail(), role);
            LoginResponse body = new LoginResponse(
                    token,
                    employee.getId(),
                    employee.getName(),
                    employee.getEmail(),
                    role
            );
            return new CustomResponse(HttpStatus.OK.value(), body, "Login successful");
        }

        if (BootstrapAuth.matches(
                employeeRepo.count(), email, password, bootstrapEmail, bootstrapPassword)) {
            String token = jwtUtil.generateToken(
                    BootstrapAuth.EMPLOYEE_ID, bootstrapEmail.trim(), Roles.SUPER_ADMIN);
            LoginResponse body = new LoginResponse(
                    token,
                    BootstrapAuth.EMPLOYEE_ID,
                    bootstrapName,
                    bootstrapEmail.trim(),
                    Roles.SUPER_ADMIN
            );
            return new CustomResponse(HttpStatus.OK.value(), body, "Bootstrap super admin login");
        }

        return new CustomResponse(HttpStatus.UNAUTHORIZED.value(), null, "Invalid email or password");
    }

    @Override
    public CustomResponse me(Long employeeId) {
        if (employeeId != null && employeeId.equals(BootstrapAuth.EMPLOYEE_ID)) {
            Map<String, Object> body = new LinkedHashMap<>();
            body.put("employeeId", BootstrapAuth.EMPLOYEE_ID);
            body.put("name", bootstrapName);
            body.put("email", bootstrapEmail.trim());
            body.put("role", Roles.SUPER_ADMIN);
            body.put("department", null);
            body.put("designation", null);
            body.put("bootstrap", true);
            return new CustomResponse(HttpStatus.OK.value(), body, "Profile fetched");
        }

        Employee employee = employeeRepo.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + employeeId));

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("employeeId", employee.getId());
        body.put("name", employee.getName());
        body.put("email", employee.getEmail());
        body.put("role", Roles.canonical(employee.getRole() != null ? employee.getRole().getName() : Roles.EMPLOYEE));
        body.put("department", employee.getDepartment() != null ? employee.getDepartment().getDepartmentName() : null);
        body.put("designation", employee.getDesignation() != null ? employee.getDesignation().getDesignationName() : null);
        return new CustomResponse(HttpStatus.OK.value(), body, "Profile fetched");
    }
}