////////////////////////////////////////////////////////ATTENDANCE CONTROLLER/////////////////////////////////////


package com.aihrms.aihrmsbackend.controller;

import com.aihrms.aihrmsbackend.dto.request.EmployeeAttendanceReq;
import com.aihrms.aihrmsbackend.dto.response.CustomResponse;
import com.aihrms.aihrmsbackend.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/attendance")
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    // 1. Employee Check-In
    // POST http://localhost:8081/attendance/check-in
    @PostMapping("/check-in")
    public ResponseEntity<?> checkIn(@RequestBody EmployeeAttendanceReq request) {
        CustomResponse response = attendanceService.checkIn(request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    // 2. Employee Check-Out
    // PUT http://localhost:8081/attendance/check-out/1
    @PutMapping("/check-out/{id}")
    public ResponseEntity<?> checkOut(@PathVariable Long id) {
        CustomResponse response = attendanceService.checkOut(id);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    // 3. Mark employee as Absent (HR/Admin)
    // POST http://localhost:8081/attendance/mark-absent
    @PostMapping("/mark-absent")
    public ResponseEntity<?> markAbsent(@RequestBody EmployeeAttendanceReq request) {
        CustomResponse response = attendanceService.markAbsent(request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    // 4. Get attendance for a specific employee
    // GET http://localhost:8081/attendance/employee/1
    @GetMapping("/employee/{empId}")
    public ResponseEntity<?> getAttendanceByEmployee(@PathVariable Long empId) {
        CustomResponse response = attendanceService.getAttendanceByEmployee(empId);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    // 5. Get all attendance records
    // GET http://localhost:8081/attendance/all
    @GetMapping("/all")
    public ResponseEntity<?> getAllAttendance() {
        CustomResponse response = attendanceService.getAllAttendance();
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    // 6. Get attendance for an employee in a date range
    // GET http://localhost:8081/attendance/employee/1/range?startDate=2025-01-01&endDate=2025-01-31
    @GetMapping("/employee/{empId}/range")
    public ResponseEntity<?> getAttendanceByDateRange(
            @PathVariable Long empId,
            @RequestParam String startDate,
            @RequestParam String endDate) {
        CustomResponse response = attendanceService.getAttendanceByEmployeeAndDateRange(empId, startDate, endDate);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    // 7. Get today's attendance for all employees
    // GET http://localhost:8081/attendance/today
    @GetMapping("/today")
    public ResponseEntity<?> getTodayAttendance() {
        CustomResponse response = attendanceService.getTodayAttendance();
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    // 8. Get attendance by status (PRESENT, ABSENT, HALF_DAY, LATE)
    // GET http://localhost:8081/attendance/status/PRESENT
    @GetMapping("/status/{status}")
    public ResponseEntity<?> getAttendanceByStatus(@PathVariable String status) {
        CustomResponse response = attendanceService.getAttendanceByStatus(status);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    // 9. Update attendance record (HR/Admin)
    // PUT http://localhost:8081/attendance/update/1
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateAttendance(@PathVariable Long id, @RequestBody EmployeeAttendanceReq request) {
        CustomResponse response = attendanceService.updateAttendance(id, request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    // 10. Delete attendance record (HR/Admin)
    // DELETE http://localhost:8081/attendance/delete/1
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteAttendance(@PathVariable Long id) {
        CustomResponse response = attendanceService.deleteAttendance(id);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}