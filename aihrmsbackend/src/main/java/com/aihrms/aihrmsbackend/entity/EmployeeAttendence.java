package com.aihrms.aihrmsbackend.entity;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name="attendence")
public class EmployeeAttendence {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @JoinColumn(name="employee_id")
    @ManyToOne
    private Employee employee;

    @Column(name="date")
    private Date date;

    @Column(name="check_In")
    private Date checkIn;

    @Column(name = "check_out")
    private Date check_Out;

    @Column(name = "status",length = 20)
    private String status;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
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

    public Date getCheck_Out() {
        return check_Out;
    }

    public void setCheck_Out(Date check_Out) {
        this.check_Out = check_Out;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
