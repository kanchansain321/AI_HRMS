package com.aihrms.aihrmsbackend.recruitment.repository;

import com.aihrms.aihrmsbackend.recruitment.entity.ChatHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatHistoryRepo extends JpaRepository<ChatHistory, Long> {
    List<ChatHistory> findByEmployeeIdOrderByCreatedAtDesc(Long employeeId);
}