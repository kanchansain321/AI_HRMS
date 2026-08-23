package com.aihrms.aihrmsbackend.entity;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "employee")
public class Employee {
   @Id
   @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

   @Column(name = "name")
    private String name;
    @Column(name="email")
    private String email;

    @Column(name="password")
    private String password;

    @JoinColumn(name="role")
    @OneToOne
    private EmpRole  role;

    @JoinColumn(name = "department_id")
    @OneToOne
    private Department department;

    @JoinColumn(name="designation_id",referencedColumnName = "id")
    @OneToOne
    private Designation designation;

    @Column(name="joining_date")
    private Date joiningDate;

    @Column(name="is_active")
    private Boolean isActive;
    @Column(name="phone_number")
 private String phoneNumber;
@Column(name="status_id")
 private Integer status;
@Column(name="created_at")
 private Date createdAt;
@Column(name="updated_at")
 private Date updatedAt;

 public Long getId() {
  return id;
 }

 public void setId(Long id) {
  this.id = id;
 }

 public String getName() {
  return name;
 }

 public void setName(String name) {
  this.name = name;
 }

 public String getEmail() {
  return email;
 }

 public void setEmail(String email) {
  this.email = email;
 }

 public String getPassword() {
  return password;
 }

 public void setPassword(String password) {
  this.password = password;
 }

 public EmpRole getRole() {
  return role;
 }

 public void setRole(EmpRole role) {
  this.role = role;
 }

 public Department getDepartment() {
  return department;
 }

 public void setDepartment(Department department) {
  this.department = department;
 }

 public void setDesignation(Designation designation) {
  this.designation = designation;
 }

 public Designation getDesignation() {
  return designation;

 }



 public Date getJoiningDate() {
  return joiningDate;
 }

 public void setJoiningDate(Date joiningDate) {
  this.joiningDate = joiningDate;
 }

 public Boolean getActive() {
  return isActive;
 }

 public void setActive(Boolean active) {
  isActive = active;


 }

 public String getPhoneNumber() {
  return phoneNumber;
 }

 public void setPhoneNumber(String phoneNumber) {
  this.phoneNumber = phoneNumber;
 }

 public Integer getStatus() {
  return status;
 }

 public void setStatus(Integer status) {
  this.status = status;
 }

 public Date getCreatedAt() {
  return createdAt;
 }

 public void setCreatedAt(Date createdAt) {
  this.createdAt = createdAt;
 }

 public Date getUpdatedAt() {
  return updatedAt;
 }

 public void setUpdatedAt(Date updatedAt) {
  this.updatedAt = updatedAt;
 }
}
