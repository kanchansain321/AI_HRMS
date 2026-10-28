package com.aihrms.aihrmsbackend.controller;

import com.aihrms.aihrmsbackend.dto.request.EmployeePayrollReq;
import com.aihrms.aihrmsbackend.dto.response.CustomResponse;
import com.aihrms.aihrmsbackend.security.AccessControl;
import com.aihrms.aihrmsbackend.security.RequireAdmin;
import com.aihrms.aihrmsbackend.service.EmpPayrollService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/employee-payroll")
public class EmpPayrollController {

    @Autowired
    private EmpPayrollService empPayrollService;

    @PostMapping("/add")
    @RequireAdmin
    public CustomResponse addPayroll(@RequestBody EmployeePayrollReq request) {
        return empPayrollService.addPayroll(request);
    }

    @GetMapping("/{payrollId}")
    @RequireAdmin
    public CustomResponse getPayrollById(@PathVariable Long payrollId) {
        return empPayrollService.getPayrollById(payrollId);
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<CustomResponse> getPayrollByEmployee(
            @PathVariable Long employeeId,
            HttpServletRequest http) {
        if (!AccessControl.canAccessEmployee(http, employeeId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(AccessControl.forbidden("You can only view your own payroll"));
        }
        return ResponseEntity.ok(empPayrollService.getPayrollByEmployee(employeeId));
    }

    @GetMapping("/all")
    @RequireAdmin
    public CustomResponse getAllPayrolls() {
        return empPayrollService.getAllPayrolls();
    }

    @DeleteMapping("/{payrollId}")
    @RequireAdmin
    public CustomResponse deletePayroll(@PathVariable Long payrollId) {
        return empPayrollService.deletePayroll(payrollId);
    }
}