package com.aihrms.aihrmsbackend.controller;

import com.aihrms.aihrmsbackend.dto.request.EmployeeAttendanceReq;
import com.aihrms.aihrmsbackend.dto.response.CustomResponse;
import com.aihrms.aihrmsbackend.security.AccessControl;
import com.aihrms.aihrmsbackend.security.RequireAdmin;
import com.aihrms.aihrmsbackend.service.AttendanceService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/attendance")
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    @PostMapping("/check-in")
    public ResponseEntity<?> checkIn(@RequestBody EmployeeAttendanceReq request, HttpServletRequest http) {
        if (!AccessControl.isAdmin(http)) {
            Long self = AccessControl.currentEmployeeId(http);
            if (self == null || self == 0L) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(AccessControl.forbidden("Bootstrap admin cannot check in — create an employee first"));
            }
            request.setEmployeeId(self);
        } else if (request.getEmployeeId() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new CustomResponse(HttpStatus.BAD_REQUEST.value(), null, "employeeId is required"));
        }
        CustomResponse response = attendanceService.checkIn(request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PutMapping("/check-out/{id}")
    public ResponseEntity<?> checkOut(@PathVariable Long id) {
        CustomResponse response = attendanceService.checkOut(id);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping("/mark-absent")
    @RequireAdmin
    public ResponseEntity<?> markAbsent(@RequestBody EmployeeAttendanceReq request) {
        CustomResponse response = attendanceService.markAbsent(request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/employee/{empId}")
    public ResponseEntity<?> getAttendanceByEmployee(@PathVariable Long empId, HttpServletRequest http) {
        if (!AccessControl.canAccessEmployee(http, empId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(AccessControl.forbidden("You can only view your own attendance"));
        }
        CustomResponse response = attendanceService.getAttendanceByEmployee(empId);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/all")
    @RequireAdmin
    public ResponseEntity<?> getAllAttendance() {
        CustomResponse response = attendanceService.getAllAttendance();
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/employee/{empId}/range")
    public ResponseEntity<?> getAttendanceByDateRange(
            @PathVariable Long empId,
            @RequestParam String startDate,
            @RequestParam String endDate,
            HttpServletRequest http) {
        if (!AccessControl.canAccessEmployee(http, empId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(AccessControl.forbidden("You can only view your own attendance"));
        }
        CustomResponse response = attendanceService.getAttendanceByEmployeeAndDateRange(empId, startDate, endDate);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/today")
    @RequireAdmin
    public ResponseEntity<?> getTodayAttendance() {
        CustomResponse response = attendanceService.getTodayAttendance();
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/status/{status}")
    @RequireAdmin
    public ResponseEntity<?> getAttendanceByStatus(@PathVariable String status) {
        CustomResponse response = attendanceService.getAttendanceByStatus(status);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PutMapping("/update/{id}")
    @RequireAdmin
    public ResponseEntity<?> updateAttendance(@PathVariable Long id, @RequestBody EmployeeAttendanceReq request) {
        CustomResponse response = attendanceService.updateAttendance(id, request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @DeleteMapping("/delete/{id}")
    @RequireAdmin
    public ResponseEntity<?> deleteAttendance(@PathVariable Long id) {
        CustomResponse response = attendanceService.deleteAttendance(id);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}