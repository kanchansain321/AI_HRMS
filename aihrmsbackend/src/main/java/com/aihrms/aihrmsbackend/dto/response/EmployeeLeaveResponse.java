//////////////////////////////////////////LEAVE DTO RESPONSE/////////////////////////////


package com.aihrms.aihrmsbackend.dto.response;

import java.util.Date;

/**
 * DTO for sending leave data back to the frontend.
 * Contains flattened employee info so the frontend doesn't
 * need to make a separate API call to get the employee name.
 */
public class EmployeeLeaveResponse {

    private Long id;              // Leave record ID
    private Long employeeId;     // Employee's ID
    private String employeeName; // Employee's name (flattened from Employee entity)
    private String leaveType;    // "SICK", "CASUAL", "EARNED"
    private Date startDate;
    private Date endDate;
    private String reason;
    private String status;       // "PENDING", "APPROVED", "REJECTED", "CANCELLED"

    // ─── GETTERS AND SETTERS ────────────────────────────────────

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getLeaveType() {
        return leaveType;
    }

    public void setLeaveType(String leaveType) {
        this.leaveType = leaveType;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}