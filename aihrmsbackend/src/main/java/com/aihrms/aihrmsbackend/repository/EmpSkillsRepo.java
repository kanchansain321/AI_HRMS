package com.aihrms.aihrmsbackend.repository;

import com.aihrms.aihrmsbackend.entity.EmpSkills;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmpSkillsRepo extends JpaRepository<EmpSkills,Long> {
}
