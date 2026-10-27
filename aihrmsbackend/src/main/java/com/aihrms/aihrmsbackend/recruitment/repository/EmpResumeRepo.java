package com.aihrms.aihrmsbackend.recruitment.repository;

import com.aihrms.aihrmsbackend.recruitment.entity.EmpResume;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmpResumeRepo extends JpaRepository<EmpResume,Long> {
}
