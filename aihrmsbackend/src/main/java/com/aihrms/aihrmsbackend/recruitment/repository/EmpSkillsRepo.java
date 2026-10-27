////////////////////////////////////repo//////////////////////////////////////



package com.aihrms.aihrmsbackend.recruitment.repository;

import com.aihrms.aihrmsbackend.recruitment.entity.EmpSkills;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmpSkillsRepo extends JpaRepository<EmpSkills, Long> {

    List<EmpSkills> findByResumeId(Long resumeId);

    void deleteByResumeId(Long resumeId);
}