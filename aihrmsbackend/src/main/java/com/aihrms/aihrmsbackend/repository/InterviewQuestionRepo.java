package com.aihrms.aihrmsbackend.repository;

import com.aihrms.aihrmsbackend.entity.InterviewQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InterviewQuestionRepo extends JpaRepository<InterviewQuestion,Long> {
}
