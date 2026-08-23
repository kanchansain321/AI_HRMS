package com.aihrms.aihrmsbackend.repository;

import com.aihrms.aihrmsbackend.entity.Department;
import org.hibernate.boot.models.JpaAnnotations;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentRepo extends JpaRepository<Department,Long> {
}
