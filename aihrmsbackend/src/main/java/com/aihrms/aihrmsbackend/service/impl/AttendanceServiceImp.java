////////////////////////////////////////////////////////ATTENDANCE SERVICE IMPL/////////////////////////////////////

package com.aihrms.aihrmsbackend.service.impl;

import com.aihrms.aihrmsbackend.dto.request.EmployeeAttendanceReq;
import com.aihrms.aihrmsbackend.dto.response.CustomResponse;
import com.aihrms.aihrmsbackend.dto.response.EmployeeAttendanceResponse;
import com.aihrms.aihrmsbackend.entity.Employee;
import com.aihrms.aihrmsbackend.entity.EmployeeAttendence;
import com.aihrms.aihrmsbackend.repository.EmployeeAttendenceRepo;
import com.aihrms.aihrmsbackend.repository.EmployeeRepo;
import com.aihrms.aihrmsbackend.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class AttendanceServiceImp implements AttendanceService {

    @Autowired
    private EmployeeAttendenceRepo attendanceRepo;

    @Autowired
    private EmployeeRepo employeeRepo;

    // ─── 1. CHECK IN ────────────────────────────────────────────
    @Override
    public CustomResponse checkIn(EmployeeAttendanceReq request) {
        try {
            // Step 1: Check if the employee exists
            Employee employee = employeeRepo.findById(request.getEmployeeId())
                    .orElseThrow(() -> new RuntimeException(
                            "Employee not found with id: " + request.getEmployeeId()));

            // Step 2: Set the date to today if not provided
            Date attendanceDate = request.getDate() != null ? request.getDate() : new Date();

            // Step 3: Check if attendance already exists for this employee today
            Optional<EmployeeAttendence> existingAttendance =
                    attendanceRepo.findByEmployeeIdAndDate(employee.getId(), attendanceDate);

            if (existingAttendance.isPresent()) {
                return new CustomResponse(
                        HttpStatus.BAD_REQUEST.value(),
                        null,
                        "Attendance already marked for this employee on this date. Use check-out instead."
                );
            }

            // Step 4: Create the attendance entity
            EmployeeAttendence attendance = new EmployeeAttendence();
            attendance.setEmployee(employee);
            attendance.setDate(attendanceDate);
            attendance.setCheckIn(request.getCheckIn() != null ? request.getCheckIn() : new Date());
            attendance.setStatus("PRESENT");

            // Step 5: Save to database
            EmployeeAttendence savedAttendance = attendanceRepo.save(attendance);

            // Step 6: Convert to response and return
            EmployeeAttendanceResponse response = mapToResponse(savedAttendance);

            return new CustomResponse(
                    HttpStatus.CREATED.value(),
                    response,
                    "Check-in recorded successfully"
            );

        } catch (RuntimeException e) {
            return new CustomResponse(
                    HttpStatus.BAD_REQUEST.value(),
                    null,
                    e.getMessage()
            );
        }
    }

    // ─── 2. CHECK OUT ───────────────────────────────────────────
    @Override
    public CustomResponse checkOut(Long attendanceId) {
        try {
            // Step 1: Find the attendance record
            EmployeeAttendence attendance = attendanceRepo.findById(attendanceId)
                    .orElseThrow(() -> new RuntimeException(
                            "Attendance record not found with id: " + attendanceId));

            // Step 2: Check if already checked out
            if (attendance.getCheck_Out() != null) {
                return new CustomResponse(
                        HttpStatus.BAD_REQUEST.value(),
                        null,
                        "Employee has already checked out for this record"
                );
            }

            // Step 3: Set check-out time to now
            Date checkOutTime = new Date();
            attendance.setCheck_Out(checkOutTime);

            // Step 4: Calculate total work hours
            if (attendance.getCheckIn() != null) {
                long diffInMillis = checkOutTime.getTime() - attendance.getCheckIn().getTime();
                double hours = diffInMillis / (1000.0 * 60 * 60);
                // Round to 2 decimal places
                hours = Math.round(hours * 100.0) / 100.0;

                // Update status based on work hours
                if (hours < 4) {
                    attendance.setStatus("HALF_DAY");
                } else if (hours >= 4 && hours < 8) {
                    attendance.setStatus("PRESENT");
                } else {
                    attendance.setStatus("PRESENT");
                }
            }

            // Step 5: Save updated record
            EmployeeAttendence savedAttendance = attendanceRepo.save(attendance);

            return new CustomResponse(
                    HttpStatus.OK.value(),
                    mapToResponse(savedAttendance),
                    "Check-out recorded successfully"
            );

        } catch (RuntimeException e) {
            return new CustomResponse(
                    HttpStatus.BAD_REQUEST.value(),
                    null,
                    e.getMessage()
            );
        }
    }

    // ─── 3. MARK ABSENT ─────────────────────────────────────────
    @Override
    public CustomResponse markAbsent(EmployeeAttendanceReq request) {
        try {
            // Step 1: Check if the employee exists
            Employee employee = employeeRepo.findById(request.getEmployeeId())
                    .orElseThrow(() -> new RuntimeException(
                            "Employee not found with id: " + request.getEmployeeId()));

            // Step 2: Set date
            Date attendanceDate = request.getDate() != null ? request.getDate() : new Date();

            // Step 3: Check if attendance already exists
            Optional<EmployeeAttendence> existingAttendance =
                    attendanceRepo.findByEmployeeIdAndDate(employee.getId(), attendanceDate);

            if (existingAttendance.isPresent()) {
                return new CustomResponse(
                        HttpStatus.BAD_REQUEST.value(),
                        null,
                        "Attendance already exists for this employee on this date"
                );
            }

            // Step 4: Create absent record
            EmployeeAttendence attendance = new EmployeeAttendence();
            attendance.setEmployee(employee);
            attendance.setDate(attendanceDate);
            attendance.setStatus("ABSENT");
            // No check-in or check-out for absent

            // Step 5: Save
            EmployeeAttendence savedAttendance = attendanceRepo.save(attendance);

            return new CustomResponse(
                    HttpStatus.CREATED.value(),
                    mapToResponse(savedAttendance),
                    "Employee marked as absent successfully"
            );

        } catch (RuntimeException e) {
            return new CustomResponse(
                    HttpStatus.BAD_REQUEST.value(),
                    null,
                    e.getMessage()
            );
        }
    }

    // ─── 4. GET ATTENDANCE BY EMPLOYEE ──────────────────────────
    @Override
    public CustomResponse getAttendanceByEmployee(Long employeeId) {
        try {
            // Verify employee exists
            employeeRepo.findById(employeeId)
                    .orElseThrow(() -> new RuntimeException(
                            "Employee not found with id: " + employeeId));

            // Fetch all attendance for this employee
            List<EmployeeAttendence> attendanceList = attendanceRepo.findByEmployeeId(employeeId);

            // Convert entity list to response list
            List<EmployeeAttendanceResponse> responseList = new ArrayList<>();
            for (EmployeeAttendence attendance : attendanceList) {
                responseList.add(mapToResponse(attendance));
            }

            CustomResponse response = new CustomResponse(
                    HttpStatus.OK.value(),
                    responseList,
                    "Attendance records fetched successfully"
            );
            response.setSize(responseList.size());

            return response;

        } catch (RuntimeException e) {
            return new CustomResponse(
                    HttpStatus.BAD_REQUEST.value(),
                    null,
                    e.getMessage()
            );
        }
    }

    // ─── 5. GET ALL ATTENDANCE ──────────────────────────────────
    @Override
    public CustomResponse getAllAttendance() {
        try {
            List<EmployeeAttendence> attendanceList = attendanceRepo.findAll();

            List<EmployeeAttendanceResponse> responseList = new ArrayList<>();
            for (EmployeeAttendence attendance : attendanceList) {
                responseList.add(mapToResponse(attendance));
            }

            CustomResponse response = new CustomResponse(
                    HttpStatus.OK.value(),
                    responseList,
                    "All attendance records fetched successfully"
            );
            response.setSize(responseList.size());

            return response;

        } catch (Exception e) {
            return new CustomResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    null,
                    "Error fetching attendance records: " + e.getMessage()
            );
        }
    }

    // ─── 6. GET ATTENDANCE BY EMPLOYEE AND DATE RANGE ───────────
    @Override
    public CustomResponse getAttendanceByEmployeeAndDateRange(Long employeeId, String startDate, String endDate) {
        try {
            // Verify employee exists
            employeeRepo.findById(employeeId)
                    .orElseThrow(() -> new RuntimeException(
                            "Employee not found with id: " + employeeId));

            // Parse date strings to Date objects
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date start = sdf.parse(startDate);
            Date end = sdf.parse(endDate);

            if (end.before(start)) {
                return new CustomResponse(
                        HttpStatus.BAD_REQUEST.value(),
                        null,
                        "End date cannot be before start date"
                );
            }

            List<EmployeeAttendence> attendanceList =
                    attendanceRepo.findByEmployeeIdAndDateBetween(employeeId, start, end);

            List<EmployeeAttendanceResponse> responseList = new ArrayList<>();
            for (EmployeeAttendence attendance : attendanceList) {
                responseList.add(mapToResponse(attendance));
            }

            CustomResponse response = new CustomResponse(
                    HttpStatus.OK.value(),
                    responseList,
                    "Attendance records fetched for date range"
            );
            response.setSize(responseList.size());

            return response;

        } catch (RuntimeException e) {
            return new CustomResponse(
                    HttpStatus.BAD_REQUEST.value(),
                    null,
                    e.getMessage()
            );
        } catch (Exception e) {
            return new CustomResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    null,
                    "Error parsing dates. Use format: yyyy-MM-dd"
            );
        }
    }

    // ─── 7. GET TODAY'S ATTENDANCE ──────────────────────────────
    @Override
    public CustomResponse getTodayAttendance() {
        try {
            // Get today's date range (start of day to end of day)
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            Date startOfDay = cal.getTime();

            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
            cal.set(Calendar.MILLISECOND, 999);
            Date endOfDay = cal.getTime();

            List<EmployeeAttendence> attendanceList =
                    attendanceRepo.findByDateBetween(startOfDay, endOfDay);

            List<EmployeeAttendanceResponse> responseList = new ArrayList<>();
            for (EmployeeAttendence attendance : attendanceList) {
                responseList.add(mapToResponse(attendance));
            }

            CustomResponse response = new CustomResponse(
                    HttpStatus.OK.value(),
                    responseList,
                    "Today's attendance fetched successfully"
            );
            response.setSize(responseList.size());

            return response;

        } catch (Exception e) {
            return new CustomResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    null,
                    "Error fetching today's attendance: " + e.getMessage()
            );
        }
    }

    // ─── 8. GET ATTENDANCE BY STATUS ────────────────────────────
    @Override
    public CustomResponse getAttendanceByStatus(String status) {
        try {
            List<EmployeeAttendence> attendanceList = attendanceRepo.findByStatus(status);

            List<EmployeeAttendanceResponse> responseList = new ArrayList<>();
            for (EmployeeAttendence attendance : attendanceList) {
                responseList.add(mapToResponse(attendance));
            }

            CustomResponse response = new CustomResponse(
                    HttpStatus.OK.value(),
                    responseList,
                    "Attendance records with status '" + status + "' fetched successfully"
            );
            response.setSize(responseList.size());

            return response;

        } catch (Exception e) {
            return new CustomResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    null,
                    "Error fetching attendance by status: " + e.getMessage()
            );
        }
    }

    // ─── 9. UPDATE ATTENDANCE ───────────────────────────────────
    @Override
    public CustomResponse updateAttendance(Long attendanceId, EmployeeAttendanceReq request) {
        try {
            // Step 1: Find existing record
            EmployeeAttendence attendance = attendanceRepo.findById(attendanceId)
                    .orElseThrow(() -> new RuntimeException(
                            "Attendance record not found with id: " + attendanceId));

            // Step 2: Update fields if provided
            if (request.getDate() != null) {
                attendance.setDate(request.getDate());
            }
            if (request.getCheckIn() != null) {
                attendance.setCheckIn(request.getCheckIn());
            }
            if (request.getCheckOut() != null) {
                attendance.setCheck_Out(request.getCheckOut());
            }
            if (request.getStatus() != null) {
                attendance.setStatus(request.getStatus());
            }

            // Step 3: Save updated record
            EmployeeAttendence savedAttendance = attendanceRepo.save(attendance);

            return new CustomResponse(
                    HttpStatus.OK.value(),
                    mapToResponse(savedAttendance),
                    "Attendance record updated successfully"
            );

        } catch (RuntimeException e) {
            return new CustomResponse(
                    HttpStatus.BAD_REQUEST.value(),
                    null,
                    e.getMessage()
            );
        }
    }

    // ─── 10. DELETE ATTENDANCE ──────────────────────────────────
    @Override
    public CustomResponse deleteAttendance(Long attendanceId) {
        try {
            // Check if record exists
            EmployeeAttendence attendance = attendanceRepo.findById(attendanceId)
                    .orElseThrow(() -> new RuntimeException(
                            "Attendance record not found with id: " + attendanceId));

            attendanceRepo.delete(attendance);

            return new CustomResponse(
                    HttpStatus.OK.value(),
                    null,
                    "Attendance record deleted successfully"
            );

        } catch (RuntimeException e) {
            return new CustomResponse(
                    HttpStatus.BAD_REQUEST.value(),
                    null,
                    e.getMessage()
            );
        }
    }

    // ─── HELPER: Convert Entity → Response DTO ──────────────────
    private EmployeeAttendanceResponse mapToResponse(EmployeeAttendence attendance) {
        EmployeeAttendanceResponse response = new EmployeeAttendanceResponse();
        response.setId(attendance.getId());
        response.setDate(attendance.getDate());
        response.setCheckIn(attendance.getCheckIn());
        response.setCheckOut(attendance.getCheck_Out());
        response.setStatus(attendance.getStatus());

        // Calculate total work hours if both check-in and check-out are present
        if (attendance.getCheckIn() != null && attendance.getCheck_Out() != null) {
            long diffInMillis = attendance.getCheck_Out().getTime() - attendance.getCheckIn().getTime();
            double hours = diffInMillis / (1000.0 * 60 * 60);
            response.setTotalWorkHours(Math.round(hours * 100.0) / 100.0);
        } else {
            response.setTotalWorkHours(0.0);
        }

        // Safely get employee info (null-check to avoid NullPointerException)
        if (attendance.getEmployee() != null) {
            response.setEmployeeId(attendance.getEmployee().getId());
            response.setEmployeeName(attendance.getEmployee().getName());
        }

        return response;
    }
}