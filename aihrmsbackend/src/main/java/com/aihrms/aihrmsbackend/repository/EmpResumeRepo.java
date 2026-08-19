package com.aihrms.aihrmsbackend.repository;

import com.aihrms.aihrmsbackend.entity.EmpResume;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmpResumeRepo extends JpaRepository<EmpResume,Long> {
}
