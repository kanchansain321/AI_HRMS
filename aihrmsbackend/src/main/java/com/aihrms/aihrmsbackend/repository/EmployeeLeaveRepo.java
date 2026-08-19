package com.aihrms.aihrmsbackend.repository;

import com.aihrms.aihrmsbackend.entity.EmployeeLeave;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeeLeaveRepo  extends JpaRepository <EmployeeLeave,Long> {
    List<EmployeeLeave> findEmployeeId(Long empoyeeId);
    List<EmployeeLeave> findByStatus(String status);

}
