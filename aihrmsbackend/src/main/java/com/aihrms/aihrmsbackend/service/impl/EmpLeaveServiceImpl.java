//////////////////////////////////////////////LEAVE SERVICE IMPL///////////////////////////////////////

package com.aihrms.aihrmsbackend.service.impl;

import com.aihrms.aihrmsbackend.dto.request.EmployeeLeaveReq;
import com.aihrms.aihrmsbackend.dto.response.CustomResponse;
import com.aihrms.aihrmsbackend.dto.response.EmployeeLeaveResponse;
import com.aihrms.aihrmsbackend.entity.Employee;
import com.aihrms.aihrmsbackend.entity.EmployeeLeave;
import com.aihrms.aihrmsbackend.repository.EmployeeLeaveRepo;
import com.aihrms.aihrmsbackend.repository.EmployeeRepo;
import com.aihrms.aihrmsbackend.service.EmpLeaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class EmpLeaveServiceImpl implements EmpLeaveService {

    @Autowired
    private EmployeeLeaveRepo employeeLeaveRepo;

    @Autowired
    private EmployeeRepo employeeRepo;

    // ─── 1. APPLY FOR LEAVE ─────────────────────────────────────
    @Override
    public CustomResponse applyLeave(EmployeeLeaveReq request) {
        try {
            // Step 1: Check if the employee exists
            Employee employee = employeeRepo.findById(request.getEmployeeId())
                    .orElseThrow(() -> new RuntimeException(
                            "Employee not found with id: " + request.getEmployeeId()));

            // Step 2: Validate dates
            if (request.getStartDate() == null || request.getEndDate() == null) {
                return new CustomResponse(
                        HttpStatus.BAD_REQUEST.value(),
                        null,
                        "Start date and end date are required"
                );
            }

            if (request.getEndDate().before(request.getStartDate())) {
                return new CustomResponse(
                        HttpStatus.BAD_REQUEST.value(),
                        null,
                        "End date cannot be before start date"
                );
            }

            // Step 3: Create the leave entity
            EmployeeLeave leave = new EmployeeLeave();
            leave.setEmployee(employee);
            leave.setLeaveType(request.getLeaveType());
            leave.setStartDate(request.getStartDate());
            leave.setEndDate(request.getEndDate());
            leave.setReason(request.getReason());
            leave.setStatus("PENDING");  // Always starts as PENDING

            // Step 4: Save to database
            EmployeeLeave savedLeave = employeeLeaveRepo.save(leave);

            // Step 5: Convert to response and return
            EmployeeLeaveResponse response = mapToResponse(savedLeave);

            return new CustomResponse(
                    HttpStatus.CREATED.value(),
                    response,
                    "Leave applied successfully"
            );

        } catch (RuntimeException e) {
            return new CustomResponse(
                    HttpStatus.BAD_REQUEST.value(),
                    null,
                    e.getMessage()
            );
        }
    }

    // ─── 2. APPROVE LEAVE ───────────────────────────────────────
    @Override
    public CustomResponse approveLeave(Long leaveId) {
        try {
            // Find the leave record
            EmployeeLeave leave = employeeLeaveRepo.findById(leaveId)
                    .orElseThrow(() -> new RuntimeException(
                            "Leave not found with id: " + leaveId));

            // Only PENDING leaves can be approved
            if (!"PENDING".equals(leave.getStatus())) {
                return new CustomResponse(
                        HttpStatus.BAD_REQUEST.value(),
                        null,
                        "Only PENDING leaves can be approved. Current status: " + leave.getStatus()
                );
            }

            // Update status
            leave.setStatus("APPROVED");
            EmployeeLeave savedLeave = employeeLeaveRepo.save(leave);

            return new CustomResponse(
                    HttpStatus.OK.value(),
                    mapToResponse(savedLeave),
                    "Leave approved successfully"
            );

        } catch (RuntimeException e) {
            return new CustomResponse(
                    HttpStatus.BAD_REQUEST.value(),
                    null,
                    e.getMessage()
            );
        }
    }

    // ─── 3. REJECT LEAVE ────────────────────────────────────────
    @Override
    public CustomResponse rejectLeave(Long leaveId) {
        try {
            EmployeeLeave leave = employeeLeaveRepo.findById(leaveId)
                    .orElseThrow(() -> new RuntimeException(
                            "Leave not found with id: " + leaveId));

            if (!"PENDING".equals(leave.getStatus())) {
                return new CustomResponse(
                        HttpStatus.BAD_REQUEST.value(),
                        null,
                        "Only PENDING leaves can be rejected. Current status: " + leave.getStatus()
                );
            }

            leave.setStatus("REJECTED");
            EmployeeLeave savedLeave = employeeLeaveRepo.save(leave);

            return new CustomResponse(
                    HttpStatus.OK.value(),
                    mapToResponse(savedLeave),
                    "Leave rejected successfully"
            );

        } catch (RuntimeException e) {
            return new CustomResponse(
                    HttpStatus.BAD_REQUEST.value(),
                    null,
                    e.getMessage()
            );
        }
    }

    // ─── 4. CANCEL LEAVE ────────────────────────────────────────
    @Override
    public CustomResponse cancelLeave(Long leaveId) {
        try {
            EmployeeLeave leave = employeeLeaveRepo.findById(leaveId)
                    .orElseThrow(() -> new RuntimeException(
                            "Leave not found with id: " + leaveId));

            // Can only cancel if still PENDING
            if (!"PENDING".equals(leave.getStatus())) {
                return new CustomResponse(
                        HttpStatus.BAD_REQUEST.value(),
                        null,
                        "Only PENDING leaves can be cancelled. Current status: " + leave.getStatus()
                );
            }

            leave.setStatus("CANCELLED");
            EmployeeLeave savedLeave = employeeLeaveRepo.save(leave);

            return new CustomResponse(
                    HttpStatus.OK.value(),
                    mapToResponse(savedLeave),
                    "Leave cancelled successfully"
            );

        } catch (RuntimeException e) {
            return new CustomResponse(
                    HttpStatus.BAD_REQUEST.value(),
                    null,
                    e.getMessage()
            );
        }
    }

    // ─── 5. GET LEAVES BY EMPLOYEE ──────────────────────────────
    @Override
    public CustomResponse getLeavesByEmployee(Long employeeId) {
        try {
            // Verify employee exists
            employeeRepo.findById(employeeId)
                    .orElseThrow(() -> new RuntimeException(
                            "Employee not found with id: " + employeeId));

            // Fetch all leaves for this employee
            List<EmployeeLeave> leaves = employeeLeaveRepo.findByEmployee_Id(employeeId);

            // Convert entity list to response list
            List<EmployeeLeaveResponse> responseList = new ArrayList<>();
            for (EmployeeLeave leave : leaves) {
                responseList.add(mapToResponse(leave));
            }

            CustomResponse response = new CustomResponse(
                    HttpStatus.OK.value(),
                    responseList,
                    "Leaves fetched successfully"
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

    // ─── 6. GET ALL LEAVES ──────────────────────────────────────
    @Override
    public CustomResponse getAllLeaves() {
        try {
            List<EmployeeLeave> leaves = employeeLeaveRepo.findAll();

            List<EmployeeLeaveResponse> responseList = new ArrayList<>();
            for (EmployeeLeave leave : leaves) {
                responseList.add(mapToResponse(leave));
            }

            CustomResponse response = new CustomResponse(
                    HttpStatus.OK.value(),
                    responseList,
                    "All leaves fetched successfully"
            );
            response.setSize(responseList.size());

            return response;

        } catch (Exception e) {
            return new CustomResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    null,
                    "Error fetching leaves: " + e.getMessage()
            );
        }
    }

    // ─── 7. GET PENDING LEAVES ──────────────────────────────────
    @Override
    public CustomResponse getPendingLeaves() {
        try {
            List<EmployeeLeave> leaves = employeeLeaveRepo.findByStatus("PENDING");

            List<EmployeeLeaveResponse> responseList = new ArrayList<>();
            for (EmployeeLeave leave : leaves) {
                responseList.add(mapToResponse(leave));
            }

            CustomResponse response = new CustomResponse(
                    HttpStatus.OK.value(),
                    responseList,
                    "Pending leaves fetched successfully"
            );
            response.setSize(responseList.size());

            return response;

        } catch (Exception e) {
            return new CustomResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    null,
                    "Error fetching pending leaves: " + e.getMessage()
            );
        }
    }

    // ─── HELPER: Convert Entity → Response DTO ──────────────────
    private EmployeeLeaveResponse mapToResponse(EmployeeLeave leave) {
        EmployeeLeaveResponse response = new EmployeeLeaveResponse();
        response.setId(leave.getId());
        response.setLeaveType(leave.getLeaveType());
        response.setStartDate(leave.getStartDate());
        response.setEndDate(leave.getEndDate());
        response.setReason(leave.getReason());
        response.setStatus(leave.getStatus());

        // Safely get employee info (null-check to avoid NullPointerException)
        if (leave.getEmployee() != null) {
            response.setEmployeeId(leave.getEmployee().getId());
            response.setEmployeeName(leave.getEmployee().getName());
        }

        return response;
    }
}