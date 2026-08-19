package com.aihrms.aihrmsbackend.repository;

import com.aihrms.aihrmsbackend.entity.ChatHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatHistoryRepo extends JpaRepository<ChatHistory,Long> {
}
