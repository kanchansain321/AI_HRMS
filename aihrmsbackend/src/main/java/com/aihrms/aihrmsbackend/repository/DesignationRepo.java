package com.aihrms.aihrmsbackend.repository;

import com.aihrms.aihrmsbackend.entity.Designation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DesignationRepo extends JpaRepository<Designation,Long> {
    Optional<Designation> findByDesignationName(String designationName);
}
