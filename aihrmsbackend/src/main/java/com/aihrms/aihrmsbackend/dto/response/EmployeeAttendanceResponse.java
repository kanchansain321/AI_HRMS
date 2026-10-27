////////////////////////////////////////////////////////ATTENDANCE DTO RESPONSE/////////////////////////////////////

package com.aihrms.aihrmsbackend.dto.response;

import java.util.Date;

/**
 * DTO for sending attendance data back to the frontend.
 * Contains flattened employee info so the frontend doesn't
 * need to make a separate API call to get the employee name.
 */
public class EmployeeAttendanceResponse {

    private Long id;              // Attendance record ID
    private Long employeeId;     // Employee's ID
    private String employeeName; // Employee's name (flattened from Employee entity)
    private Date date;           // Attendance date
    private Date checkIn;        // Check-in time
    private Date checkOut;       // Check-out time
    private String status;       // "PRESENT", "ABSENT", "HALF_DAY", "LATE"
    private Double totalWorkHours; // Calculated work hours

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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getCheckIn() {
        return checkIn;
    }

    public void setCheckIn(Date checkIn) {
        this.checkIn = checkIn;
    }

    public Date getCheckOut() {
        return checkOut;
    }

    public void setCheckOut(Date checkOut) {
        this.checkOut = checkOut;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getTotalWorkHours() {
        return totalWorkHours;
    }

    public void setTotalWorkHours(Double totalWorkHours) {
        this.totalWorkHours = totalWorkHours;
    }
}