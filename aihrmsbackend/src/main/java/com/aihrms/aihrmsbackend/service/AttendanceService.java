////////////////////////////////////////////////////////ATTENDANCE SERVICE////////////////////////////////////////////

package com.aihrms.aihrmsbackend.service;

import com.aihrms.aihrmsbackend.dto.request.EmployeeAttendanceReq;
import com.aihrms.aihrmsbackend.dto.response.CustomResponse;

/**
 * Service interface for Attendance Management.
 *
 * Think of this as a "menu" — it lists what operations are available,
 * but doesn't say HOW they work. The "how" is in AttendanceServiceImp.
 */
public interface AttendanceService {

    // Employee checks in for the day
    CustomResponse checkIn(EmployeeAttendanceReq request);

    // Employee checks out for the day
    CustomResponse checkOut(Long attendanceId);

    // Mark an employee as absent (HR/Admin)
    CustomResponse markAbsent(EmployeeAttendanceReq request);

    // Get all attendance records for a specific employee
    CustomResponse getAttendanceByEmployee(Long employeeId);

    // Get ALL attendance records (for HR/admin dashboard)
    CustomResponse getAllAttendance();

    // Get attendance for a specific employee between two dates
    CustomResponse getAttendanceByEmployeeAndDateRange(Long employeeId, String startDate, String endDate);

    // Get today's attendance for all employees
    CustomResponse getTodayAttendance();

    // Get attendance by status (PRESENT, ABSENT, HALF_DAY, LATE)
    CustomResponse getAttendanceByStatus(String status);

    // Update an attendance record (HR/Admin)
    CustomResponse updateAttendance(Long attendanceId, EmployeeAttendanceReq request);

    // Delete an attendance record (HR/Admin)
    CustomResponse deleteAttendance(Long attendanceId);
}