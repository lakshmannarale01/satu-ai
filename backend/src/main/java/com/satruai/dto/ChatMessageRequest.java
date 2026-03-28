package com.satruai.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for sending a chat message from frontend
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageRequest {
    
    // Session ID to identify the conversation
    private String sessionId;
    
    // The user's message
    private String message;
    
    // Language of the message (en, hi, mr)
    private String language = "en";
}

