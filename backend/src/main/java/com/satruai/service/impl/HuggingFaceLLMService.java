package com.satruai.service.impl;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.satruai.service.LLMService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Hugging Face LLM Service Implementation
 * Uses Hugging Face Inference API for chat responses
 * Free tier available: https://huggingface.co
 */
@Service
@Slf4j
public class HuggingFaceLLMService implements LLMService {
    
    @Value("${hf.api.key}")
    private String apiKey;
    
    private final RestTemplate restTemplate;
    private final Gson gson;
    
    // Hugging Face free models
    private static final String MODEL = "meta-llama/Llama-2-7b-chat-hf";
    private static final String API_URL = "https://api-inference.huggingface.co/models/";
    
    public HuggingFaceLLMService() {
        this.restTemplate = new RestTemplate();
        this.gson = new Gson();
    }
    
    @Override
    public String chat(String userMessage, String language) {
        if (!isReady()) {
            log.warn("Hugging Face API key not configured");
            return "I'm not available right now. Please try again later.";
        }
        
        try {
            log.info("Sending message to Hugging Face: {}", userMessage);
            
            // Create prompt with language context
            String prompt = buildPrompt(userMessage, language);
            
            // Call Hugging Face API
            String response = callHuggingFaceAPI(prompt);
            
            if (response != null && !response.isEmpty()) {
                log.info("Got response from Hugging Face");
                return cleanResponse(response);
            }
            
            return "I'm thinking... Please try again!";
            
        } catch (Exception e) {
            log.error("Error calling Hugging Face API", e);
            return "Sorry, I'm having trouble right now. Please try again!";
        }
    }
    
    @Override
    public boolean isReady() {
        return apiKey != null && !apiKey.isEmpty() && !apiKey.equals("your-hf-api-key");
    }
    
    /**
     * Build prompt with language context
     */
    private String buildPrompt(String userMessage, String language) {
        String languageName = getLanguageName(language);
        return String.format(
            "You are Satu, a friendly AI assistant. Respond in %s. " +
            "User: %s\n" +
            "Satu:",
            languageName,
            userMessage
        );
    }
    
    /**
     * Call Hugging Face Inference API
     */
    private String callHuggingFaceAPI(String prompt) throws Exception {
        try {
            String url = API_URL + MODEL;
            
            // Create request
            JsonObject requestBody = new JsonObject();
            requestBody.addProperty("inputs", prompt);
            requestBody.addProperty("parameters", "");
            
            // Make HTTP request
            String payload = gson.toJson(requestBody);
            
            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.set("Authorization", "Bearer " + apiKey);
            headers.set("Content-Type", "application/json");
            
            org.springframework.http.HttpEntity<String> request = 
                new org.springframework.http.HttpEntity<>(payload, headers);
            
            org.springframework.http.ResponseEntity<String> response = 
                restTemplate.postForEntity(url, request, String.class);
            
            if (response.getStatusCode().is2xxSuccessful()) {
                // Parse response
                com.google.gson.JsonArray jsonArray = 
                    gson.fromJson(response.getBody(), com.google.gson.JsonArray.class);
                
                if (jsonArray != null && jsonArray.size() > 0) {
                    String generatedText = jsonArray.get(0)
                        .getAsJsonObject()
                        .get("generated_text")
                        .getAsString();
                    return generatedText;
                }
            }
            
        } catch (Exception e) {
            log.error("Error calling Hugging Face API: {}", e.getMessage());
            throw e;
        }
        
        return null;
    }
    
    /**
     * Clean response text
     */
    private String cleanResponse(String response) {
        // Remove prompt from response
        if (response.contains("Satu:")) {
            response = response.substring(response.indexOf("Satu:") + 5).trim();
        }
        
        // Remove extra whitespace
        response = response.replaceAll("\\s+", " ").trim();
        
        // Limit length
        if (response.length() > 500) {
            response = response.substring(0, 500) + "...";
        }
        
        return response;
    }
    
    /**
     * Get language name
     */
    private String getLanguageName(String language) {
        if (language == null) return "English";
        
        return switch (language.toLowerCase()) {
            case "hi" -> "Hindi (हिंदी)";
            case "mr" -> "Marathi (मराठी)";
            default -> "English";
        };
    }
}

