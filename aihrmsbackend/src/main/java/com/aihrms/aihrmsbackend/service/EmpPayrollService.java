package com.aihrms.aihrmsbackend.service;

import com.aihrms.aihrmsbackend.dto.request.EmployeePayrollReq;
import com.aihrms.aihrmsbackend.dto.response.CustomResponse;

public interface EmpPayrollService {

    CustomResponse addPayroll(EmployeePayrollReq request);

    CustomResponse getPayrollById(Long payrollId);

    CustomResponse getPayrollByEmployee(Long employeeId);

    CustomResponse getAllPayrolls();

    CustomResponse deletePayroll(Long payrollId);
}