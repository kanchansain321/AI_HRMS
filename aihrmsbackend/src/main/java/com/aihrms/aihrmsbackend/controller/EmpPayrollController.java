package com.aihrms.aihrmsbackend.controller;

import com.aihrms.aihrmsbackend.dto.request.EmployeePayrollReq;
import com.aihrms.aihrmsbackend.dto.response.CustomResponse;
import com.aihrms.aihrmsbackend.service.EmpPayrollService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/employee-payroll")
public class EmpPayrollController {

    @Autowired
    private EmpPayrollService empPayrollService;

    @PostMapping("/add")
    public CustomResponse addPayroll(
            @RequestBody EmployeePayrollReq request) {
        return empPayrollService.addPayroll(request);
    }

    @GetMapping("/{payrollId}")
    public CustomResponse getPayrollById(
            @PathVariable Long payrollId) {
        return empPayrollService.getPayrollById(payrollId);
    }

    @GetMapping("/employee/{employeeId}")
    public CustomResponse getPayrollByEmployee(
            @PathVariable Long employeeId) {
        return empPayrollService.getPayrollByEmployee(employeeId);
    }

    @GetMapping("/all")
    public CustomResponse getAllPayrolls() {
        return empPayrollService.getAllPayrolls();
    }

    @DeleteMapping("/{payrollId}")
    public CustomResponse deletePayroll(
            @PathVariable Long payrollId) {
        return empPayrollService.deletePayroll(payrollId);
    }
}