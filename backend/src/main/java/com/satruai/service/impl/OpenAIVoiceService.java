package com.satruai.service.impl;

import com.satruai.service.VoiceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * OpenAI implementation of VoiceService
 * Uses OpenAI Whisper API for STT (Speech-to-Text)
 * Uses OpenAI TTS API for Text-to-Speech
 */
@Slf4j
@Service
public class OpenAIVoiceService implements VoiceService {

    @Value("${llm.api.key:}")
    private String apiKey;

    @Value("${voice.storage.path:./audio}")
    private String audioStoragePath;

    @Value("${voice.whisper.model:whisper-1}")
    private String whisperModel;

    @Value("${voice.tts.model:tts-1}")
    private String ttsModel;

    @Value("${voice.tts.voice:nova}")
    private String defaultVoice;

    @Value("${voice.storage.max-file-size:26214400}")
    private Long maxFileSize; // 25MB default

    private final RestTemplate restTemplate = new RestTemplate();

    private static final String WHISPER_API_URL = "https://api.openai.com/v1/audio/transcriptions";
    private static final String TTS_API_URL = "https://api.openai.com/v1/audio/speech";
    private static final String SUPPORTED_AUDIO_TYPES = "audio/mpeg,audio/wav,audio/m4a,audio/ogg";
    private static final long MAX_AUDIO_DURATION = 60000; // 1 minute in milliseconds

    @Override
    public VoiceTranscribeResponse transcribeAudio(MultipartFile audioFile, String language, String sessionId) throws Exception {
        log.info("Transcribing audio file: {} for session: {}", audioFile.getOriginalFilename(), sessionId);

        // Validate audio file
        if (!validateAudioFile(audioFile)) {
            String error = "Invalid audio file format or size";
            log.warn(error);
            return new VoiceTranscribeResponse(error);
        }

        try {
            // Create multipart request for Whisper API
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(apiKey);
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", audioFile.getResource());
            body.add("model", whisperModel);
            body.add("language", mapLanguageCode(language));

            HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);

            // Call Whisper API
            ResponseEntity<WhisperResponse> response = restTemplate.postForEntity(
                    WHISPER_API_URL,
                    request,
                    WhisperResponse.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                WhisperResponse whisperResponse = response.getBody();
                
                // Save audio file locally
                String audioPath = saveAudioFile(audioFile, sessionId);
                
                log.info("Successfully transcribed audio: {}", whisperResponse.text);
                
                VoiceTranscribeResponse result = new VoiceTranscribeResponse(
                        whisperResponse.text,
                        language,
                        0.95, // OpenAI doesn't provide confidence, use default
                        audioFile.getSize()
                );
                return result;
            } else {
                String error = "Whisper API returned error status: " + response.getStatusCode();
                log.error(error);
                return new VoiceTranscribeResponse(error);
            }

        } catch (Exception e) {
            String error = "Failed to transcribe audio: " + e.getMessage();
            log.error(error, e);
            return new VoiceTranscribeResponse(error);
        }
    }

    @Override
    public VoiceSynthesizeResponse synthesizeSpeech(String text, String language, String voiceCharacter) throws Exception {
        log.info("Synthesizing speech: '{}' for language: {}", text.substring(0, Math.min(50, text.length())), language);

        try {
            if (!isReady()) {
                return new VoiceSynthesizeResponse("Voice service not ready - API key not configured");
            }

            // Map language to voice if needed
            String voice = voiceCharacter != null ? voiceCharacter : defaultVoice;
            String mappedLanguageVoice = mapLanguageToVoice(language, voice);

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(apiKey);
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Build TTS request body
            String jsonBody = String.format(
                    "{\"model\":\"%s\",\"input\":\"%s\",\"voice\":\"%s\"}",
                    ttsModel,
                    escapeJson(text),
                    mappedLanguageVoice
            );

            HttpEntity<String> request = new HttpEntity<>(jsonBody, headers);

            // Call TTS API
            ResponseEntity<byte[]> response = restTemplate.postForEntity(
                    TTS_API_URL,
                    request,
                    byte[].class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                // Save audio file
                String audioPath = saveAudioContent(response.getBody(), language);
                
                log.info("Successfully synthesized speech, saved to: {}", audioPath);
                
                return new VoiceSynthesizeResponse(
                        "/api/voice/stream/" + new File(audioPath).getName(),
                        "audio/mpeg",
                        (long)(text.length() * 50) // Rough estimate: ~50ms per character
                );
            } else {
                return new VoiceSynthesizeResponse("TTS API returned error status: " + response.getStatusCode());
            }

        } catch (Exception e) {
            log.error("Failed to synthesize speech: {}", e.getMessage(), e);
            return new VoiceSynthesizeResponse("Failed to synthesize speech: " + e.getMessage());
        }
    }

    @Override
    public boolean validateAudioFile(MultipartFile audioFile) {
        if (audioFile == null || audioFile.isEmpty()) {
            log.warn("Audio file is empty");
            return false;
        }

        // Check file size
        if (audioFile.getSize() > maxFileSize) {
            log.warn("Audio file too large: {} bytes", audioFile.getSize());
            return false;
        }

        // Check content type
        String contentType = audioFile.getContentType();
        if (contentType == null || !SUPPORTED_AUDIO_TYPES.contains(contentType)) {
            log.warn("Unsupported audio format: {}", contentType);
            return false;
        }

        // Check file name extension
        String filename = audioFile.getOriginalFilename();
        if (filename == null || (!filename.endsWith(".mp3") && !filename.endsWith(".wav") && 
            !filename.endsWith(".m4a") && !filename.endsWith(".ogg"))) {
            log.warn("Unsupported file extension: {}", filename);
            return false;
        }

        return true;
    }

    @Override
    public boolean isReady() {
        return apiKey != null && !apiKey.isEmpty() && !apiKey.equals("your-api-key-here");
    }

    @Override
    public String[] getSupportedVoices(String language) {
        // OpenAI TTS supports: alloy, echo, fable, onyx, nova, shimmer
        // For multilingual, map to appropriate voices
        return new String[]{"nova", "onyx", "alloy", "echo", "fable", "shimmer"};
    }

    // ==================== HELPER METHODS ====================

    /**
     * Map language code to OpenAI Whisper language parameter
     */
    private String mapLanguageCode(String language) {
        if ("hi".equals(language)) {
            return "hi";  // Hindi
        } else if ("mr".equals(language)) {
            return "mr";  // Marathi
        } else {
            return "en";  // English
        }
    }

    /**
     * Map language and voice to OpenAI TTS supported voice
     */
    private String mapLanguageToVoice(String language, String voice) {
        // OpenAI TTS voices: alloy, echo, fable, onyx, nova, shimmer
        // For now, map languages to a default voice (can be extended)
        // In production, you might want language-specific voices from different providers
        String baseVoice = voice != null ? voice : "nova";
        
        // Validate voice is supported
        String[] supported = getSupportedVoices(language);
        for (String s : supported) {
            if (s.equals(baseVoice)) {
                return baseVoice;
            }
        }
        
        // Default to nova if voice not found
        return "nova";
    }

    /**
     * Save uploaded audio file to disk
     */
    private String saveAudioFile(MultipartFile audioFile, String sessionId) throws IOException {
        // Create storage directory if not exists
        Path storagePath = Paths.get(audioStoragePath);
        Files.createDirectories(storagePath);

        // Create filename with timestamp
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS"));
        String filename = String.format("audio_%s_%s_%s", sessionId, timestamp, audioFile.getOriginalFilename());
        Path filePath = storagePath.resolve(filename);

        // Save file
        Files.copy(audioFile.getInputStream(), filePath);
        log.debug("Saved audio file: {}", filePath);

        return filePath.toString();
    }

    /**
     * Save synthesized audio content to disk
     */
    private String saveAudioContent(byte[] audioContent, String language) throws IOException {
        // Create storage directory if not exists
        Path storagePath = Paths.get(audioStoragePath);
        Files.createDirectories(storagePath);

        // Create filename with timestamp
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS"));
        String filename = String.format("tts_%s_%s.mp3", language, timestamp);
        Path filePath = storagePath.resolve(filename);

        // Save file
        Files.write(filePath, audioContent);
        log.debug("Saved TTS audio file: {}", filePath);

        return filePath.toString();
    }

    /**
     * Escape special characters in JSON string
     */
    private String escapeJson(String text) {
        return text
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    // ==================== OPENAI API RESPONSE CLASSES ====================

    /**
     * Response from OpenAI Whisper API
     */
    static class WhisperResponse {
        public String text;
        public String language;
        public Double duration;
    }

    /**
     * Response from OpenAI TTS API
     */
    static class TTSResponse {
        public byte[] audio;
        public String contentType;
    }
}

