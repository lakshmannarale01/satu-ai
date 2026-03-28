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

    // VOICE SUPPORT FIELDS (Step 3)
    
    // Message type: TEXT, VOICE, or AUDIO
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageType messageType = MessageType.TEXT;
    
    // URL or path to audio file (for voice messages)
    private String audioUrl;
    
    // Transcription of voice message (speech-to-text result)
    private String transcription;
    
    // Voice character used for TTS (e.g., "nova", "onyx")
    private String voiceCharacter = "nova";
    
    // Local file path where audio is stored on server
    private String audioFilePath;
    
    // Duration of audio in milliseconds
    private Long audioDuration;
    
    // Whether this is a generated TTS message
    private Boolean isTextToSpeech = false;

    /**
     * Enum for message types to support text, voice, and audio
     */
    public enum MessageType {
        TEXT,       // Regular text message
        VOICE,      // User spoke it (has transcription)
        AUDIO       // Audio response (TTS generated)
    }
}

