package com.aihrms.aihrmsbackend.controller;

import com.aihrms.aihrmsbackend.dto.request.EmployeeLeaveReq;
import com.aihrms.aihrmsbackend.dto.response.CustomResponse;
import com.aihrms.aihrmsbackend.security.AccessControl;
import com.aihrms.aihrmsbackend.security.RequireAdmin;
import com.aihrms.aihrmsbackend.service.EmpLeaveService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/leave")
public class EmpLeaveController {

    @Autowired
    private EmpLeaveService leaveService;

    @PostMapping("/apply")
    public ResponseEntity<?> applyLeave(@RequestBody EmployeeLeaveReq request, HttpServletRequest http) {
        if (!AccessControl.isAdmin(http)) {
            Long self = AccessControl.currentEmployeeId(http);
            if (self == null || self == 0L) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(AccessControl.forbidden("Bootstrap admin cannot apply leave — create an employee first"));
            }
            request.setEmployeeId(self);
        } else if (request.getEmployeeId() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new CustomResponse(HttpStatus.BAD_REQUEST.value(), null, "employeeId is required"));
        }
        CustomResponse response = leaveService.applyLeave(request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PutMapping("/approve/{id}")
    @RequireAdmin
    public ResponseEntity<?> approveLeave(@PathVariable Long id) {
        CustomResponse response = leaveService.approveLeave(id);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PutMapping("/reject/{id}")
    @RequireAdmin
    public ResponseEntity<?> rejectLeave(@PathVariable Long id) {
        CustomResponse response = leaveService.rejectLeave(id);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PutMapping("/cancel/{id}")
    public ResponseEntity<?> cancelLeave(@PathVariable Long id) {
        CustomResponse response = leaveService.cancelLeave(id);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/employee/{empId}")
    public ResponseEntity<?> getLeavesByEmployee(@PathVariable Long empId, HttpServletRequest http) {
        if (!AccessControl.canAccessEmployee(http, empId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(AccessControl.forbidden("You can only view your own leaves"));
        }
        CustomResponse response = leaveService.getLeavesByEmployee(empId);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/all")
    @RequireAdmin
    public ResponseEntity<?> getAllLeaves() {
        CustomResponse response = leaveService.getAllLeaves();
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/pending")
    @RequireAdmin
    public ResponseEntity<?> getPendingLeaves() {
        CustomResponse response = leaveService.getPendingLeaves();
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}