package com.satruai.repository;

import com.satruai.model.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for Conversation entity
 */
@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    
    // Find conversation by session ID
    Optional<Conversation> findBySessionId(String sessionId);
}

