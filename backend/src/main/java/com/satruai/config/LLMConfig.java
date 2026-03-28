package com.satruai.config;

import com.satruai.service.impl.DemoLLMService;
import com.satruai.service.impl.OpenAILLMService;
import com.satruai.service.impl.HuggingFaceLLMService;
import com.satruai.service.LLMService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * LLM Service Configuration
 * Determines which LLM service to use:
 * 1. Hugging Face (if HF_API_KEY is set)
 * 2. OpenAI (if LLM_API_KEY is set)
 * 3. Demo Mode (default - no API key needed)
 */
@Configuration
@Slf4j
public class LLMConfig {
    
    @Value("${llm.demo-mode:true}")
    private Boolean demoMode;
    
    @Value("${llm.api.key:}")
    private String openaiApiKey;
    
    @Value("${hf.api.key:}")
    private String huggingFaceApiKey;
    
    @Bean
    public LLMService llmService() {
        // Priority 1: Check for Hugging Face API key
        if (huggingFaceApiKey != null && !huggingFaceApiKey.isEmpty() && 
            !huggingFaceApiKey.equals("your-hf-api-key")) {
            log.info("🤗 Hugging Face Mode Enabled - Using Hugging Face Inference API");
            return new HuggingFaceLLMService();
        }
        
        // Priority 2: Check for OpenAI API key
        if (openaiApiKey != null && !openaiApiKey.isEmpty() && 
            !openaiApiKey.equals("your-api-key-here")) {
            log.info("🔌 OpenAI Mode Enabled - Using OpenAI API");
            return new OpenAILLMService();
        }
        
        // Priority 3: Default to Demo Mode
        log.info("🎭 Demo Mode Enabled - Using mock AI responses (no API key needed)");
        return new DemoLLMService();
    }
}


