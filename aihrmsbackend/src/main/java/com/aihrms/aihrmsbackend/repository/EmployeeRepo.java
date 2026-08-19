package com.aihrms.aihrmsbackend.repository;

import com.aihrms.aihrmsbackend.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepo extends JpaRepository<Employee,Long> {
    Optional<Employee> findByEmail(String email);

    boolean existsByEmail(String email);

    List<Employee> findByDepartment(Integer department);

    List<Employee> findByDesignation(Integer designation);

    List<Employee> findByRole(String role);

    List<Employee> findByIsActiveTrue();

    List<Employee> findByStatus(Integer status);



}
