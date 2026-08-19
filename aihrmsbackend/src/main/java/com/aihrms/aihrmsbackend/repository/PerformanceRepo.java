package com.aihrms.aihrmsbackend.repository;

import com.aihrms.aihrmsbackend.entity.Performance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PerformanceRepo extends JpaRepository<Performance,Long> {
}
