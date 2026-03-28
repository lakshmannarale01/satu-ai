package com.satruai.repository;

import com.satruai.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for Message entity
 */
@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    
    // Find all messages for a conversation, ordered by timestamp
    List<Message> findByConversationIdOrderByTimestampAsc(Long conversationId);
}

