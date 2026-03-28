package com.satruai.service;

/**
 * Interface for LLM (Large Language Model) integration
 * Abstraction to allow different AI providers
 */
public interface LLMService {
    
    /**
     * Send a message to the LLM and get a response
     * @param userMessage The user's message
     * @param language The language of the message (en, hi, mr)
     * @return AI response
     */
    String chat(String userMessage, String language);
    
    /**
     * Check if the LLM service is properly configured
     * @return true if ready, false otherwise
     */
    boolean isReady();
}

