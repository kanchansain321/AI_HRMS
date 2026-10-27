package com.aihrms.aihrmsbackend.service.impl;

import com.aihrms.aihrmsbackend.dto.request.EmployeePayrollReq;
import com.aihrms.aihrmsbackend.dto.response.CustomResponse;
import com.aihrms.aihrmsbackend.dto.response.EmployeePayrollResponse;
import com.aihrms.aihrmsbackend.entity.Employee;
import com.aihrms.aihrmsbackend.entity.EmployeePayroll;
import com.aihrms.aihrmsbackend.repository.EmployeePayrollRepo;
import com.aihrms.aihrmsbackend.repository.EmployeeRepo;
import com.aihrms.aihrmsbackend.service.EmpPayrollService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class EmpPayrollServiceImpl implements EmpPayrollService {

    @Autowired
    private EmployeePayrollRepo employeePayrollRepo;

    @Autowired
    private EmployeeRepo employeeRepo;

    // 1. ADD PAYROLL
    @Override
    public CustomResponse addPayroll(EmployeePayrollReq request) {
        try {
            Employee employee = employeeRepo
                    .findById(request.getEmployeeId())
                    .orElseThrow(() -> new RuntimeException(
                            "Employee not found with id: "
                                    + request.getEmployeeId()));

            if (request.getBasicSalary() == null
                    || request.getBasicSalary()
                    .compareTo(BigDecimal.ZERO) < 0) {
                return new CustomResponse(
                        HttpStatus.BAD_REQUEST.value(),
                        null,
                        "Basic salary must be zero or greater"
                );
            }

            BigDecimal deductions = request.getDeductions() == null
                    ? BigDecimal.ZERO
                    : request.getDeductions();

            if (deductions.compareTo(BigDecimal.ZERO) < 0
                    || deductions.compareTo(
                    request.getBasicSalary()) > 0) {
                return new CustomResponse(
                        HttpStatus.BAD_REQUEST.value(),
                        null,
                        "Deductions must be between zero and basic salary"
                );
            }

            if (request.getMonth() == null
                    || request.getMonth().trim().isEmpty()
                    || request.getYear() == null) {
                return new CustomResponse(
                        HttpStatus.BAD_REQUEST.value(),
                        null,
                        "Month and year are required"
                );
            }

            EmployeePayroll payroll = new EmployeePayroll();
            payroll.setEmployee(employee);
            payroll.setBasicSalary(request.getBasicSalary());
            payroll.setDeductions(deductions);
            payroll.setNetSalary(
                    request.getBasicSalary().subtract(deductions));
            payroll.setMonth(request.getMonth());
            payroll.setYear(request.getYear());

            EmployeePayroll saved =
                    employeePayrollRepo.save(payroll);

            return new CustomResponse(
                    HttpStatus.CREATED.value(),
                    mapToResponse(saved),
                    "Payroll added successfully"
            );

        } catch (RuntimeException e) {
            return new CustomResponse(
                    HttpStatus.BAD_REQUEST.value(),
                    null,
                    e.getMessage()
            );
        }
    }

    // 2. GET PAYROLL BY ID
    @Override
    public CustomResponse getPayrollById(Long payrollId) {
        return employeePayrollRepo.findById(payrollId)
                .map(payroll -> new CustomResponse(
                        HttpStatus.OK.value(),
                        mapToResponse(payroll),
                        "Payroll fetched successfully"))
                .orElseGet(() -> new CustomResponse(
                        HttpStatus.NOT_FOUND.value(),
                        null,
                        "Payroll not found with id: " + payrollId));
    }

    // 3. GET PAYROLL BY EMPLOYEE
    @Override
    public CustomResponse getPayrollByEmployee(Long employeeId) {
        if (!employeeRepo.existsById(employeeId)) {
            return new CustomResponse(
                    HttpStatus.NOT_FOUND.value(),
                    null,
                    "Employee not found with id: " + employeeId);
        }

        List<EmployeePayrollResponse> responseList = new ArrayList<>();

        for (EmployeePayroll payroll :
                employeePayrollRepo.findByEmployee_Id(employeeId)) {
            responseList.add(mapToResponse(payroll));
        }

        CustomResponse response = new CustomResponse(
                HttpStatus.OK.value(),
                responseList,
                "Employee payroll fetched successfully");

        response.setSize(responseList.size());
        return response;
    }

    // 4. GET ALL PAYROLLS
    @Override
    public CustomResponse getAllPayrolls() {
        List<EmployeePayrollResponse> responseList = new ArrayList<>();

        for (EmployeePayroll payroll : employeePayrollRepo.findAll()) {
            responseList.add(mapToResponse(payroll));
        }

        CustomResponse response = new CustomResponse(
                HttpStatus.OK.value(),
                responseList,
                "All payrolls fetched successfully");

        response.setSize(responseList.size());
        return response;
    }

    // 5. DELETE PAYROLL
    @Override
    public CustomResponse deletePayroll(Long payrollId) {
        if (!employeePayrollRepo.existsById(payrollId)) {
            return new CustomResponse(
                    HttpStatus.NOT_FOUND.value(),
                    null,
                    "Payroll not found with id: " + payrollId);
        }

        employeePayrollRepo.deleteById(payrollId);

        return new CustomResponse(
                HttpStatus.OK.value(),
                null,
                "Payroll deleted successfully");
    }

    // ENTITY TO RESPONSE DTO
    private EmployeePayrollResponse mapToResponse(
            EmployeePayroll payroll) {

        EmployeePayrollResponse response =
                new EmployeePayrollResponse();

        response.setId(payroll.getId());
        response.setBasicSalary(payroll.getBasicSalary());
        response.setDeductions(payroll.getDeductions());
        response.setNetSalary(payroll.getNetSalary());
        response.setMonth(payroll.getMonth());
        response.setYear(payroll.getYear());

        if (payroll.getEmployee() != null) {
            response.setEmployeeId(
                    payroll.getEmployee().getId());
            response.setEmployeeName(
                    payroll.getEmployee().getName());
        }

        return response;
    }
}