/////////////////////////////////////////repo/////////////////////////////////////


package com.aihrms.aihrmsbackend.recruitment.repository;

import com.aihrms.aihrmsbackend.recruitment.entity.InterviewQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InterviewQuestionRepo extends JpaRepository<InterviewQuestion, Long> {

    List<InterviewQuestion> findByResumeId(Long resumeId);

    void deleteByResumeId(Long resumeId);
}