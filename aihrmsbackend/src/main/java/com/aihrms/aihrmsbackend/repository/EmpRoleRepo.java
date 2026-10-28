package com.aihrms.aihrmsbackend.repository;

import com.aihrms.aihrmsbackend.entity.EmpRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmpRoleRepo extends JpaRepository<EmpRole, Long> {

    Optional<EmpRole> findFirstByNameIgnoreCase(String name);
}