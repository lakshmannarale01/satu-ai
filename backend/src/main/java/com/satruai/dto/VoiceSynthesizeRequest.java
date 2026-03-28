package com.satruai.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for voice synthesis request (Text-to-Speech)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VoiceSynthesizeRequest {
    
    // The text to convert to speech
    private String text;
    
    // Language for synthesis ("en", "hi", "mr")
    private String language = "en";
    
    // Voice character to use (e.g., "nova", "onyx")
    private String voice = "nova";
    
    // Speech speed (0.25 - 4.0)
    private Double speed = 1.0;
    
    // Session ID (optional)
    private String sessionId;
}

