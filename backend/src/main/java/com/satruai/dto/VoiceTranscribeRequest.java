package com.satruai.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for voice transcription request (Speech-to-Text)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VoiceTranscribeRequest {
    
    // The audio file (handled as multipart/form-data)
    private String audioFileBase64; // Base64 encoded audio
    
    // Language of the audio ("en", "hi", "mr")
    private String language = "en";
    
    // Session ID to attach transcription to conversation
    private String sessionId;
    
    // Audio format (mp3, wav, m4a, etc)
    private String format;
    
    // Duration in milliseconds
    private Long duration;
}

