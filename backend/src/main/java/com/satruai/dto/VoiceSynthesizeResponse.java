package com.satruai.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for voice synthesis response (Text-to-Speech result)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VoiceSynthesizeResponse {
    
    // URL or path to stream the synthesized audio
    private String audioUrl;
    
    // MIME type of the audio (e.g., "audio/mpeg")
    private String contentType;
    
    // Duration of synthesized audio in milliseconds
    private Long duration;
    
    // ID of the message created in database
    private Long messageId;
    
    // Language of synthesis
    private String language;
    
    // Voice used
    private String voice;
    
    // Any error message if synthesis failed
    private String error;
}

