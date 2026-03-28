package com.satruai.controller;

import com.satruai.dto.ChatMessageRequest;
import com.satruai.dto.ChatMessageResponse;
import com.satruai.model.Conversation;
import com.satruai.service.ChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for Chat API
 * Handles conversation creation, message sending, and history retrieval
 */
@RestController
@RequestMapping("/api/chat")
@Slf4j
public class ChatController {
    
    @Autowired
    private ChatService chatService;
    
    /**
     * Create a new conversation session
     * GET /api/chat/new?language=en
     */
    @GetMapping("/new")
    public ResponseEntity<?> createNewConversation(
            @RequestParam(defaultValue = "en") String language) {
        try {
            log.info("Creating new conversation with language: {}", language);
            Conversation conversation = chatService.createConversation(language);
            
            Map<String, Object> response = new HashMap<>();
            response.put("sessionId", conversation.getSessionId());
            response.put("language", conversation.getLanguage());
            response.put("createdAt", conversation.getCreatedAt());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error creating conversation: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to create conversation"));
        }
    }
    
    /**
     * Send a message and get AI response
     * POST /api/chat/send
     * Body: { "sessionId": "...", "message": "...", "language": "en" }
     */
    @PostMapping("/send")
    public ResponseEntity<?> sendMessage(@RequestBody ChatMessageRequest request) {
        try {
            log.debug("Received message: {}", request.getMessage());
            
            // Validate request
            if (request.getMessage() == null || request.getMessage().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Message cannot be empty"));
            }
            
            ChatMessageResponse response = chatService.sendMessage(request);
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            log.warn("Invalid argument: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error sending message: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to process message"));
        }
    }
    
    /**
     * Get conversation history
     * GET /api/chat/history/{sessionId}
     */
    @GetMapping("/history/{sessionId}")
    public ResponseEntity<?> getConversationHistory(@PathVariable String sessionId) {
        try {
            log.debug("Fetching history for session: {}", sessionId);
            List<ChatMessageResponse> messages = chatService.getConversationHistory(sessionId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("sessionId", sessionId);
            response.put("messages", messages);
            response.put("count", messages.size());
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            log.warn("Conversation not found: {}", sessionId);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error fetching history: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to retrieve conversation history"));
        }
    }
    
    /**
     * Health check endpoint to verify if LLM service is ready
     * GET /api/chat/health
     */
    @GetMapping("/health")
    public ResponseEntity<?> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "healthy");
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }
}

