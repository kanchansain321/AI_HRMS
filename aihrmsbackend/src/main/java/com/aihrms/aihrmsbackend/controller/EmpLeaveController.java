////////////////////////////////////////////////////////LEAVE CONTROLLER/////////////////////////////////////


package com.aihrms.aihrmsbackend.controller;

import com.aihrms.aihrmsbackend.dto.request.EmployeeLeaveReq;
import com.aihrms.aihrmsbackend.dto.response.CustomResponse;
import com.aihrms.aihrmsbackend.service.EmpLeaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/leave")
public class EmpLeaveController {

    @Autowired
    private EmpLeaveService leaveService;

    // 1. Apply for leave
    // POST http://localhost:8081/leave/apply
    @PostMapping("/apply")
    public ResponseEntity<?> applyLeave(@RequestBody EmployeeLeaveReq request) {
        CustomResponse response = leaveService.applyLeave(request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    // 2. Approve leave (HR/Manager)
    // PUT http://localhost:8081/leave/approve/1
    @PutMapping("/approve/{id}")
    public ResponseEntity<?> approveLeave(@PathVariable Long id) {
        CustomResponse response = leaveService.approveLeave(id);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    // 3. Reject leave (HR/Manager)
    // PUT http://localhost:8081/leave/reject/1
    @PutMapping("/reject/{id}")
    public ResponseEntity<?> rejectLeave(@PathVariable Long id) {
        CustomResponse response = leaveService.rejectLeave(id);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    // 4. Cancel leave (Employee)
    // PUT http://localhost:8081/leave/cancel/1
    @PutMapping("/cancel/{id}")
    public ResponseEntity<?> cancelLeave(@PathVariable Long id) {
        CustomResponse response = leaveService.cancelLeave(id);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    // 5. Get all leaves for a specific employee
    // GET http://localhost:8081/leave/employee/1
    @GetMapping("/employee/{empId}")
    public ResponseEntity<?> getLeavesByEmployee(@PathVariable Long empId) {
        CustomResponse response = leaveService.getLeavesByEmployee(empId);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    // 6. Get all leave records
    // GET http://localhost:8081/leave/all
    @GetMapping("/all")
    public ResponseEntity<?> getAllLeaves() {
        CustomResponse response = leaveService.getAllLeaves();
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    // 7. Get pending leave requests (for HR dashboard)
    // GET http://localhost:8081/leave/pending
    @GetMapping("/pending")
    public ResponseEntity<?> getPendingLeaves() {
        CustomResponse response = leaveService.getPendingLeaves();
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}