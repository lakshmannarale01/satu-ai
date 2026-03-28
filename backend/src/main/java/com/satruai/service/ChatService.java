package com.satruai.service;

import com.satruai.dto.ChatMessageRequest;
import com.satruai.dto.ChatMessageResponse;
import com.satruai.model.Conversation;
import com.satruai.model.Message;
import com.satruai.repository.ConversationRepository;
import com.satruai.repository.MessageRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for handling chat operations
 */
@Service
@Slf4j
public class ChatService {
    
    @Autowired
    private ConversationRepository conversationRepository;
    
    @Autowired
    private MessageRepository messageRepository;
    
    @Autowired
    private LLMService llmService;
    
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    /**
     * Create a new conversation session
     */
    public Conversation createConversation(String language) {
        String sessionId = UUID.randomUUID().toString();
        Conversation conversation = new Conversation();
        conversation.setSessionId(sessionId);
        conversation.setLanguage(language);
        conversation.setTitle("Chat Session");
        conversation.setCreatedAt(LocalDateTime.now());
        
        log.info("Creating new conversation with session ID: {}", sessionId);
        return conversationRepository.save(conversation);
    }
    
    /**
     * Send a message and get AI response
     */
    public ChatMessageResponse sendMessage(ChatMessageRequest request) {
        long startTime = System.currentTimeMillis();
        
        // Find or create conversation
        Conversation conversation = conversationRepository.findBySessionId(request.getSessionId())
            .orElseGet(() -> createConversation(request.getLanguage()));
        
        log.debug("Processing message for session: {}", request.getSessionId());
        
        // Save user message
        Message userMessage = new Message();
        userMessage.setConversation(conversation);
        userMessage.setSender("user");
        userMessage.setContent(request.getMessage());
        userMessage.setLanguage(request.getLanguage());
        userMessage.setTimestamp(LocalDateTime.now());
        messageRepository.save(userMessage);
        
        // Get AI response
        String aiResponse = llmService.chat(request.getMessage(), request.getLanguage());
        
        // Save AI response
        Message assistantMessage = new Message();
        assistantMessage.setConversation(conversation);
        assistantMessage.setSender("assistant");
        assistantMessage.setContent(aiResponse);
        assistantMessage.setLanguage(request.getLanguage());
        assistantMessage.setAiModel("gpt-3.5-turbo");
        assistantMessage.setTimestamp(LocalDateTime.now());
        long responseTime = System.currentTimeMillis() - startTime;
        assistantMessage.setResponseTime(responseTime);
        
        Message savedAssistantMessage = messageRepository.save(assistantMessage);
        
        // Update conversation timestamp
        conversation.setLastUpdatedAt(LocalDateTime.now());
        conversationRepository.save(conversation);
        
        log.info("Message processed in {} ms", responseTime);
        
        // Return response
        return convertToDTO(savedAssistantMessage);
    }
    
    /**
     * Get all messages for a conversation
     */
    public List<ChatMessageResponse> getConversationHistory(String sessionId) {
        Conversation conversation = conversationRepository.findBySessionId(sessionId)
            .orElseThrow(() -> new IllegalArgumentException("Conversation not found: " + sessionId));
        
        List<Message> messages = messageRepository.findByConversationIdOrderByTimestampAsc(conversation.getId());
        return messages.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    /**
     * Convert Message entity to ChatMessageResponse DTO
     */
    private ChatMessageResponse convertToDTO(Message message) {
        return new ChatMessageResponse(
            message.getId(),
            message.getConversation().getSessionId(),
            message.getSender(),
            message.getContent(),
            message.getLanguage(),
            message.getTimestamp().format(dateFormatter),
            message.getAiModel()
        );
    }
}

