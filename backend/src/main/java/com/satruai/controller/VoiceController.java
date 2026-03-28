package com.satruai.controller;

import com.satruai.dto.ChatMessageResponse;
import com.satruai.model.Conversation;
import com.satruai.model.Message;
import com.satruai.repository.ConversationRepository;
import com.satruai.repository.MessageRepository;
import com.satruai.service.ChatService;
import com.satruai.service.LLMService;
import com.satruai.service.VoiceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * REST Controller for voice operations
 * Handles speech-to-text transcription, text-to-speech synthesis, and audio playback
 */
@Slf4j
@RestController
@RequestMapping("/api/voice")
@CrossOrigin(origins = "http://localhost:3000")
public class VoiceController {

    @Autowired
    private VoiceService voiceService;

    @Autowired
    private ChatService chatService;

    @Autowired
    private LLMService llmService;

    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private MessageRepository messageRepository;

    /**
     * Transcribe audio file to text (Speech-to-Text)
     * 
     * @param audio The audio file to transcribe
     * @param language The language of audio ("en", "hi", "mr")
     * @param sessionId The conversation session ID
     * @return Transcribed text and metadata
     */
    @PostMapping("/transcribe")
    public ResponseEntity<?> transcribeAudio(
            @RequestParam("audio") MultipartFile audio,
            @RequestParam(value = "language", defaultValue = "en") String language,
            @RequestParam(value = "sessionId", required = false) String sessionId) {

        log.info("Received transcription request for language: {}, session: {}", language, sessionId);

        try {
            // Validate voice service is ready
            if (!voiceService.isReady()) {
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                        .body(Map.of("error", "Voice service not configured"));
            }

            // Transcribe audio
            VoiceService.VoiceTranscribeResponse transcribeResponse = voiceService.transcribeAudio(audio, language, sessionId);

            if (transcribeResponse.error != null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", transcribeResponse.error));
            }

            // If sessionId provided, save transcribed message to database
            if (sessionId != null && !sessionId.isEmpty()) {
                Optional<Conversation> convOpt = conversationRepository.findBySessionId(sessionId);
                
                if (convOpt.isPresent()) {
                    Conversation conversation = convOpt.get();
                    
                    // Create user message with transcription
                    Message userMessage = new Message();
                    userMessage.setConversation(conversation);
                    userMessage.setSender("user");
                    userMessage.setContent(transcribeResponse.text);
                    userMessage.setTranscription(transcribeResponse.text);
                    userMessage.setMessageType(Message.MessageType.VOICE);
                    userMessage.setLanguage(language);
                    userMessage.setTimestamp(LocalDateTime.now());
                    userMessage.setAudioDuration(transcribeResponse.duration);
                    
                    Message savedUserMessage = messageRepository.save(userMessage);
                    log.info("Saved transcribed message: {}", savedUserMessage.getId());
                    
                    // Get AI response to transcribed text
                    long startTime = System.currentTimeMillis();
                    String aiResponse = llmService.chat(transcribeResponse.text, language);
                    long responseTime = System.currentTimeMillis() - startTime;
                    
                    // Create AI response message with TTS
                    Message aiMessage = new Message();
                    aiMessage.setConversation(conversation);
                    aiMessage.setSender("assistant");
                    aiMessage.setContent(aiResponse);
                    aiMessage.setLanguage(language);
                    aiMessage.setTimestamp(LocalDateTime.now());
                    aiMessage.setAiModel("gpt-3.5-turbo");
                    aiMessage.setResponseTime(responseTime);
                    aiMessage.setMessageType(Message.MessageType.TEXT); // Will be synthesized on demand
                    
                    Message savedAIMessage = messageRepository.save(aiMessage);
                    log.info("Saved AI response message: {}", savedAIMessage.getId());
                    
                    // Return response with both messages
                    return ResponseEntity.ok(Map.of(
                            "userMessage", Map.of(
                                    "id", savedUserMessage.getId(),
                                    "text", transcribeResponse.text,
                                    "language", language,
                                    "timestamp", savedUserMessage.getTimestamp()
                            ),
                            "aiMessage", Map.of(
                                    "id", savedAIMessage.getId(),
                                    "text", aiResponse,
                                    "language", language,
                                    "timestamp", savedAIMessage.getTimestamp()
                            )
                    ));
                }
            }

            // Return just the transcription if no session
            return ResponseEntity.ok(Map.of(
                    "text", transcribeResponse.text,
                    "language", language,
                    "duration", transcribeResponse.duration,
                    "confidence", transcribeResponse.confidence
            ));

        } catch (Exception e) {
            log.error("Error transcribing audio: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to transcribe audio: " + e.getMessage()));
        }
    }

    /**
     * Synthesize text to speech (Text-to-Speech)
     * 
     * @param text The text to convert to speech
     * @param language The language for synthesis ("en", "hi", "mr")
     * @param voice The voice to use (optional)
     * @return Audio file URL and metadata
     */
    @PostMapping("/synthesize")
    public ResponseEntity<?> synthesizeSpeech(
            @RequestParam("text") String text,
            @RequestParam(value = "language", defaultValue = "en") String language,
            @RequestParam(value = "voice", defaultValue = "nova") String voice,
            @RequestParam(value = "sessionId", required = false) String sessionId) {

        log.info("Received synthesis request for text: '{}', language: {}", 
                text.substring(0, Math.min(50, text.length())), language);

        try {
            // Validate voice service is ready
            if (!voiceService.isReady()) {
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                        .body(Map.of("error", "Voice service not configured"));
            }

            // Synthesize speech
            VoiceService.VoiceSynthesizeResponse synthesizeResponse = voiceService.synthesizeSpeech(text, language, voice);

            if (synthesizeResponse.error != null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", synthesizeResponse.error));
            }

            // If sessionId provided, save TTS message to database
            if (sessionId != null && !sessionId.isEmpty()) {
                Optional<Conversation> convOpt = conversationRepository.findBySessionId(sessionId);
                
                if (convOpt.isPresent()) {
                    Conversation conversation = convOpt.get();
                    
                    // Create TTS message
                    Message ttsMessage = new Message();
                    ttsMessage.setConversation(conversation);
                    ttsMessage.setSender("assistant");
                    ttsMessage.setContent(text);
                    ttsMessage.setAudioUrl(synthesizeResponse.audioUrl);
                    ttsMessage.setAudioDuration(synthesizeResponse.duration);
                    ttsMessage.setVoiceCharacter(voice);
                    ttsMessage.setMessageType(Message.MessageType.AUDIO);
                    ttsMessage.setLanguage(language);
                    ttsMessage.setTimestamp(LocalDateTime.now());
                    ttsMessage.setIsTextToSpeech(true);
                    
                    Message savedMessage = messageRepository.save(ttsMessage);
                    log.info("Saved TTS message: {}", savedMessage.getId());
                    
                    return ResponseEntity.ok(Map.of(
                            "messageId", savedMessage.getId(),
                            "audioUrl", synthesizeResponse.audioUrl,
                            "duration", synthesizeResponse.duration,
                            "language", language,
                            "timestamp", savedMessage.getTimestamp()
                    ));
                }
            }

            // Return just the audio URL if no session
            return ResponseEntity.ok(Map.of(
                    "audioUrl", synthesizeResponse.audioUrl,
                    "duration", synthesizeResponse.duration,
                    "contentType", synthesizeResponse.contentType
            ));

        } catch (Exception e) {
            log.error("Error synthesizing speech: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to synthesize speech: " + e.getMessage()));
        }
    }

    /**
     * Stream audio file for playback
     * 
     * @param filename The audio file name to stream
     * @return Audio file content
     */
    @GetMapping("/stream/{filename}")
    public ResponseEntity<?> streamAudio(@PathVariable String filename) {
        log.debug("Streaming audio file: {}", filename);

        try {
            // Validate filename to prevent path traversal attacks
            if (filename.contains("..") || filename.contains("/") || filename.contains("\\")) {
                return ResponseEntity.badRequest().body("Invalid filename");
            }

            // Construct file path
            String filePath = Paths.get("./audio", filename).toString();
            File audioFile = new File(filePath);

            if (!audioFile.exists()) {
                log.warn("Audio file not found: {}", filePath);
                return ResponseEntity.notFound().build();
            }

            Resource resource = new FileSystemResource(audioFile);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("audio/mpeg"))
                    .contentLength(audioFile.length())
                    .body(resource);

        } catch (Exception e) {
            log.error("Error streaming audio: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to stream audio"));
        }
    }

    /**
     * Get supported voices for a language
     * 
     * @param language The language code
     * @return Array of supported voices
     */
    @GetMapping("/voices/{language}")
    public ResponseEntity<?> getSupportedVoices(@PathVariable String language) {
        try {
            String[] voices = voiceService.getSupportedVoices(language);
            return ResponseEntity.ok(Map.of(
                    "language", language,
                    "voices", voices
            ));
        } catch (Exception e) {
            log.error("Error getting voices: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get voices"));
        }
    }

    /**
     * Delete a voice message
     * 
     * @param messageId The message ID to delete
     * @return Success status
     */
    @DeleteMapping("/{messageId}")
    public ResponseEntity<?> deleteVoiceMessage(@PathVariable Long messageId) {
        log.info("Deleting voice message: {}", messageId);

        try {
            Optional<Message> msgOpt = messageRepository.findById(messageId);
            
            if (msgOpt.isPresent()) {
                Message message = msgOpt.get();
                
                // Delete audio file if exists
                if (message.getAudioFilePath() != null) {
                    File audioFile = new File(message.getAudioFilePath());
                    if (audioFile.exists() && audioFile.delete()) {
                        log.debug("Deleted audio file: {}", message.getAudioFilePath());
                    }
                }
                
                // Delete message from database
                messageRepository.delete(message);
                log.info("Deleted voice message: {}", messageId);
                
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.notFound().build();
            }
            
        } catch (Exception e) {
            log.error("Error deleting voice message: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to delete message"));
        }
    }

    /**
     * Health check for voice service
     * 
     * @return Service status
     */
    @GetMapping("/health")
    public ResponseEntity<?> voiceServiceHealth() {
        boolean isReady = voiceService.isReady();
        return ResponseEntity.ok(Map.of(
                "status", isReady ? "healthy" : "degraded",
                "voiceServiceReady", isReady,
                "timestamp", LocalDateTime.now()
        ));
    }
}

