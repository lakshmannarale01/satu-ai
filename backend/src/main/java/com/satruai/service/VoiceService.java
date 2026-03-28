package com.satruai.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * Interface for voice operations: Speech-to-Text and Text-to-Speech
 * Abstraction allows for multiple provider implementations (OpenAI, Google Cloud, Azure, etc.)
 */
public interface VoiceService {

    /**
     * Transcribe audio to text using Speech-to-Text API
     * 
     * @param audioFile The audio file to transcribe
     * @param language The language of the audio ("en", "hi", "mr")
     * @param sessionId The conversation session ID
     * @return VoiceTranscribeResponse containing transcribed text and metadata
     * @throws Exception if transcription fails
     */
    VoiceTranscribeResponse transcribeAudio(MultipartFile audioFile, String language, String sessionId) throws Exception;

    /**
     * Synthesize text to speech using Text-to-Speech API
     * 
     * @param text The text to convert to speech
     * @param language The language for synthesis ("en", "hi", "mr")
     * @param voiceCharacter The voice to use (e.g., "nova", "onyx")
     * @return VoiceSynthesizeResponse containing audio file path and metadata
     * @throws Exception if synthesis fails
     */
    VoiceSynthesizeResponse synthesizeSpeech(String text, String language, String voiceCharacter) throws Exception;

    /**
     * Validate audio file format and size
     * 
     * @param audioFile The file to validate
     * @return true if file is valid, false otherwise
     */
    boolean validateAudioFile(MultipartFile audioFile);

    /**
     * Check if voice service is ready (API keys configured, etc.)
     * 
     * @return true if service is operational
     */
    boolean isReady();

    /**
     * Get supported voices for a given language
     * 
     * @param language The language code
     * @return Array of available voice names
     */
    String[] getSupportedVoices(String language);

    /**
     * Response class for transcription operations
     */
    class VoiceTranscribeResponse {
        public String text;
        public String language;
        public Double confidence;
        public Long duration;
        public String error;

        public VoiceTranscribeResponse(String text, String language, Double confidence, Long duration) {
            this.text = text;
            this.language = language;
            this.confidence = confidence;
            this.duration = duration;
        }

        public VoiceTranscribeResponse(String error) {
            this.error = error;
        }
    }

    /**
     * Response class for synthesis operations
     */
    class VoiceSynthesizeResponse {
        public String audioUrl;
        public String contentType;
        public Long duration;
        public String error;

        public VoiceSynthesizeResponse(String audioUrl, String contentType, Long duration) {
            this.audioUrl = audioUrl;
            this.contentType = contentType;
            this.duration = duration;
        }

        public VoiceSynthesizeResponse(String error) {
            this.error = error;
        }
    }
}

