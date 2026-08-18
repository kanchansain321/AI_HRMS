package com.aihrms.aihrmsbackend.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name ="payroll")
public class EmployeePayroll {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @Column(name = "employee_id")
    private  Integer Employee_id;
    @Column(name = "basic_salary")
    private BigDecimal basicSalary;
    @Column(precision = 10,scale = 2)
    private BigDecimal deductions;

    @Column(name = "net_salary")
    private BigDecimal netSalary;
    @Column(length = 20)
    private String month;
    private Integer year;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getEmployee_id() {
        return Employee_id;
    }

    public void setEmployee_id(Integer employee_id) {
        Employee_id = employee_id;
    }

    public BigDecimal getBasicSalary() {
        return basicSalary;
    }

    public void setBasicSalary(BigDecimal basicSalary) {
        this.basicSalary = basicSalary;
    }

    public BigDecimal getDeductions() {
        return deductions;
    }

    public void setDeductions(BigDecimal deductions) {
        this.deductions = deductions;
    }

    public BigDecimal getNetSalary() {
        return netSalary;
    }

    public void setNetSalary(BigDecimal netSalary) {
        this.netSalary = netSalary;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }
}
