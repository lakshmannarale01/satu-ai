package com.satruai.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for chat message response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageResponse {
    
    // Message ID
    private Long id;
    
    // Session ID
    private String sessionId;
    
    // Who sent: "user" or "assistant"
    private String sender;
    
    // The message content
    private String content;
    
    // Language of the message
    private String language;
    
    // Timestamp
    private String timestamp;
    
    // AI model used (if assistant)
    private String aiModel;
}

