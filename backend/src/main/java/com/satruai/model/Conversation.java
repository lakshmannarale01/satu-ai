package com.satruai.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a conversation session between user and Satu AI
 */
@Entity
@Table(name = "conversations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Conversation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Session identifier for tracking conversations
    @Column(nullable = false, unique = true)
    private String sessionId;
    
    // User's preferred language (en, hi, mr)
    @Column(nullable = false)
    private String language = "en";
    
    // Conversation title/subject
    private String title;
    
    // When the conversation started
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    // Last activity timestamp
    private LocalDateTime lastUpdatedAt;
    
    // One-to-many relationship with messages
    @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Message> messages = new ArrayList<>();
    
    // Flag to indicate if conversation is active
    private Boolean isActive = true;
}

