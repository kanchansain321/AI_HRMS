package com.aihrms.aihrmsbackend.repository;

import com.aihrms.aihrmsbackend.entity.EmployeeAttendence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface EmployeeAttendenceRepo extends JpaRepository<EmployeeAttendence,Long> {
    List<EmployeeAttendence> findByEmployeeId(Long employeeid);
    List<EmployeeAttendence> findEmployeeIdAndDateBetween(Long employeeId, Date start,Date end);

}
