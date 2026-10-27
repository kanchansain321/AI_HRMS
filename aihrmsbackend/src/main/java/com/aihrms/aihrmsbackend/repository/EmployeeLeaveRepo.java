package com.aihrms.aihrmsbackend.repository;

import com.aihrms.aihrmsbackend.entity.EmployeeLeave;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface EmployeeLeaveRepo  extends JpaRepository <EmployeeLeave,Long> {
    // Spring Data JPA auto-generates the SQL query from the method name!
    // This becomes: SELECT * FROM emp_leave WHERE employee_id = ?
    List<EmployeeLeave> findByEmployee_Id(Long employeeId);

    // This becomes: SELECT * FROM emp_leave WHERE status = ?
    List<EmployeeLeave> findByStatus(String status);

}
