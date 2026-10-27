////////////////////////////////////////////////////////LEAVE SERVICE////////////////////////////////////////////



package com.aihrms.aihrmsbackend.service;

import com.aihrms.aihrmsbackend.dto.request.EmployeeLeaveReq;
import com.aihrms.aihrmsbackend.dto.response.CustomResponse;

/**
 * Service interface for Leave Management.
 *
 * Think of this as a "menu" — it lists what operations are available,
 * but doesn't say HOW they work. The "how" is in LeaveServiceImpl.
 */
public interface EmpLeaveService {

    // Employee applies for leave
    CustomResponse applyLeave(EmployeeLeaveReq request);

    // HR/Manager approves a leave request
    CustomResponse approveLeave(Long leaveId);

    // HR/Manager rejects a leave request
    CustomResponse rejectLeave(Long leaveId);

    // Employee cancels their own leave request (only if still PENDING)
    CustomResponse cancelLeave(Long leaveId);

    // Get all leaves for a specific employee
    CustomResponse getLeavesByEmployee(Long employeeId);

    // Get ALL leave records (for HR/admin dashboard)
    CustomResponse getAllLeaves();

    // Get only PENDING leaves (for HR to review)
    CustomResponse getPendingLeaves();
}