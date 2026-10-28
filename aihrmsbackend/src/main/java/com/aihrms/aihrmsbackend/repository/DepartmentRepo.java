package com.aihrms.aihrmsbackend.repository;

import com.aihrms.aihrmsbackend.entity.Department;
import org.hibernate.boot.models.JpaAnnotations;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DepartmentRepo extends JpaRepository<Department,Integer> {
    Optional<Department> findByDepartmentName(String name);
}
