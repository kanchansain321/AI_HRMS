package com.aihrms.aihrmsbackend.repository;

import com.aihrms.aihrmsbackend.entity.EmployeePayroll;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeePayrollRepo extends JpaRepository<EmployeePayroll,Long> {
}
