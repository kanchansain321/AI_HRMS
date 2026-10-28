package com.aihrms.aihrmsbackend.controller;

import com.aihrms.aihrmsbackend.dto.request.EmployeeAddUpdateReq;
import com.aihrms.aihrmsbackend.dto.response.CustomResponse;
import com.aihrms.aihrmsbackend.dto.response.EmployeeResponse;
import com.aihrms.aihrmsbackend.security.AccessControl;
import com.aihrms.aihrmsbackend.security.RequireAdmin;
import com.aihrms.aihrmsbackend.service.EmployeeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @PostMapping
    @RequireAdmin
    public ResponseEntity<EmployeeResponse> createEmployee(@Valid @RequestBody EmployeeAddUpdateReq request) {
        EmployeeResponse response = employeeService.createEmployee(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<?> getEmployeeById(@RequestParam("empId") Long empId, HttpServletRequest request) {
        if (!AccessControl.canAccessEmployee(request, empId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(AccessControl.forbidden("You can only view your own profile"));
        }
        EmployeeResponse response = employeeService.findByIdEmployee(empId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/all")
    @RequireAdmin
    public ResponseEntity<?> getAllEmployee() {
        List<EmployeeResponse> responseList = employeeService.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(responseList);
    }

    @PutMapping
    @RequireAdmin
    public ResponseEntity<?> updateEmployee(@Valid @RequestBody EmployeeAddUpdateReq request) {
        if (request.getId() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new CustomResponse(HttpStatus.BAD_REQUEST.value(), null, "id is required"));
        }
        EmployeeResponse response = employeeService.updateEmployee(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}