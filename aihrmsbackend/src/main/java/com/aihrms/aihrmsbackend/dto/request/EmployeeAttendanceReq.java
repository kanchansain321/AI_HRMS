////////////////////////////////////////////////////////ATTENDANCE REQUEST DTO/////////////////////////////////////

package com.aihrms.aihrmsbackend.dto.request;

import java.util.Date;

/**
 * DTO for marking attendance (check-in / check-out).
 * This is what the frontend/Postman sends to us.
 */
public class EmployeeAttendanceReq {

    private Long employeeId;    // Which employee is marking attendance
    private Date date;          // The attendance date
    private Date checkIn;       // Check-in time
    private Date checkOut;      // Check-out time (can be null during check-in)
    private String status;      // "PRESENT", "ABSENT", "HALF_DAY", "LATE"

    // ─── GETTERS AND SETTERS ────────────────────────────────────

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
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
}