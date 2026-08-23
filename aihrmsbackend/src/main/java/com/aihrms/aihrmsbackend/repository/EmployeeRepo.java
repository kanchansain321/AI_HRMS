package com.aihrms.aihrmsbackend.repository;

import com.aihrms.aihrmsbackend.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepo extends JpaRepository<Employee,Long> {

    @Query(value = "select e from  Employee  e where  e.email=:email and e.isActive=true")
    List<Employee> existsByEmail(String email) ;

}
