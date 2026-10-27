
package com.aihrms.aihrmsbackend.dto.request;

import java.util.Date;

/**
 * DTO for applying for leave.
 * This is what the frontend/Postman sends to us.
 */
public class EmployeeLeaveReq {

    private Long employeeId;    // Which employee is applying
    private String leaveType;   // "SICK", "CASUAL", "EARNED" etc.
    private Date startDate;     // Leave start date
    private Date endDate;       // Leave end date
    private String reason;      // Why they need leave

    // ─── GETTERS AND SETTERS ────────────────────────────────────

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
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
}