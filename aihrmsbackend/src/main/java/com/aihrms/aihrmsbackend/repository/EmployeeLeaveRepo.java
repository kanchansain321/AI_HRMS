package com.aihrms.aihrmsbackend.repository;

import com.aihrms.aihrmsbackend.entity.EmployeeLeave;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface EmployeeLeaveRepo  extends JpaRepository <EmployeeLeave,Long> {

}
