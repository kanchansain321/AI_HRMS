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

    @Column(name="role")
    private String role;

    @Column(name = "department_id")
    private Integer department;

    @Column(name="designation_id")
    private Integer designation;

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

 public String getRole() {
  return role;
 }

 public void setRole(String role) {
  this.role = role;
 }

 public Integer getDepartment() {
  return department;
 }

 public void setDepartment(Integer department) {
  this.department = department;
 }

 public Integer getDesignation() {
  return designation;
 }

 public void setDesignation(Integer designation) {
  this.designation = designation;
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
