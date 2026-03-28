package com.satruai.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for voice transcription response (Speech-to-Text result)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VoiceTranscribeResponse {
    
    // The transcribed text
    private String text;
    
    // Language of the transcription
    private String language;
    
    // Confidence score (0-1)
    private Double confidence;
    
    // Duration of audio in milliseconds
    private Long duration;
    
    // ID of the message created in database
    private Long messageId;
    
    // Any error message if transcription failed
    private String error;
}

