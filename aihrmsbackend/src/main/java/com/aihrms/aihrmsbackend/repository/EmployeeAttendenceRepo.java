////////////////////////////////////////////////////////ATTENDANCE REPOSITORY/////////////////////////////////////

package com.aihrms.aihrmsbackend.repository;

import com.aihrms.aihrmsbackend.entity.EmployeeAttendence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeAttendenceRepo extends JpaRepository<EmployeeAttendence, Long> {

    // Find all attendance records for a specific employee
    List<EmployeeAttendence> findByEmployeeId(Long employeeId);

    // Find attendance by employee and specific date
    Optional<EmployeeAttendence> findByEmployeeIdAndDate(Long employeeId, Date date);

    // Find attendance records by status (PRESENT, ABSENT, HALF_DAY, LATE)
    List<EmployeeAttendence> findByStatus(String status);

    // Find attendance records between two dates
    List<EmployeeAttendence> findByDateBetween(Date startDate, Date endDate);

    // Find attendance for a specific employee between two dates
    List<EmployeeAttendence> findByEmployeeIdAndDateBetween(Long employeeId, Date startDate, Date endDate);
}