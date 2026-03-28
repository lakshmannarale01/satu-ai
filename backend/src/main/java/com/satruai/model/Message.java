package com.satruai.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Represents a single message in a conversation
 * Can be from user or from Satu AI
 */
@Entity
@Table(name = "messages")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // The conversation this message belongs to
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id", nullable = false)
    private Conversation conversation;
    
    // WHO sent the message: "user" or "assistant"
    @Column(nullable = false)
    private String sender; // "user" or "assistant"
    
    // The actual message content
    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String content;
    
    // Language of the message (en, hi, mr)
    @Column(nullable = false)
    private String language = "en";
    
    // When the message was sent
    @Column(nullable = false)
    private LocalDateTime timestamp = LocalDateTime.now();
    
    // For AI responses, store the model used
    private String aiModel;
    
    // Optional: For tracking response time
    private Long responseTime; // in milliseconds
}

