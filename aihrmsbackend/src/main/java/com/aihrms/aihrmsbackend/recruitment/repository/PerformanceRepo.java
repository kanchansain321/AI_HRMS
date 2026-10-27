package com.aihrms.aihrmsbackend.recruitment.repository;

import com.aihrms.aihrmsbackend.recruitment.entity.Performance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PerformanceRepo extends JpaRepository<Performance,Long> {
}
