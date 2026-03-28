package com.satruai.service.impl;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.satruai.service.LLMService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * OpenAI LLM Service Implementation
 * Integrates with OpenAI's GPT API for chat responses
 */
@Service
@Slf4j
public class OpenAILLMService implements LLMService {
    
    @Value("${llm.api.key}")
    private String apiKey;
    
    @Value("${llm.api.url}")
    private String apiUrl;
    
    @Value("${llm.model}")
    private String model;
    
    @Value("${llm.max-tokens}")
    private Integer maxTokens;
    
    @Value("${llm.temperature}")
    private Double temperature;
    
    private final RestTemplate restTemplate;
    private final Gson gson;
    
    public OpenAILLMService() {
        this.restTemplate = new RestTemplate();
        this.gson = new Gson();
    }
    
    @Override
    public String chat(String userMessage, String language) {
        if (!isReady()) {
            log.warn("LLM service not properly configured");
            return "I'm currently not available. Please check back later.";
        }
        
        try {
            // Create request body for OpenAI API
            JsonObject requestBody = new JsonObject();
            requestBody.addProperty("model", model);
            requestBody.addProperty("temperature", temperature);
            requestBody.addProperty("max_tokens", maxTokens);
            
            // Add messages array with system prompt and user message
            JsonArray messages = new JsonArray();
            
            // System prompt to make Satu behave like a best friend
            JsonObject systemMessage = new JsonObject();
            systemMessage.addProperty("role", "system");
            String systemPrompt = getSystemPrompt(language);
            systemMessage.addProperty("content", systemPrompt);
            messages.add(systemMessage);
            
            // User message
            JsonObject userMsg = new JsonObject();
            userMsg.addProperty("role", "user");
            userMsg.addProperty("content", userMessage);
            messages.add(userMsg);
            
            requestBody.add("messages", messages);
            
            log.debug("Sending request to OpenAI API");
            
            // Make API call with proper headers
            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.set("Authorization", "Bearer " + apiKey);
            headers.set("Content-Type", "application/json");
            
            org.springframework.http.HttpEntity<String> entity = 
                new org.springframework.http.HttpEntity<>(requestBody.toString(), headers);
            
            org.springframework.http.ResponseEntity<String> response = 
                restTemplate.postForEntity(apiUrl, entity, String.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                // Parse response
                JsonObject responseJson = gson.fromJson(response.getBody(), JsonObject.class);
                return responseJson.getAsJsonArray("choices")
                    .get(0).getAsJsonObject()
                    .getAsJsonObject("message")
                    .get("content").getAsString();
            } else {
                log.error("OpenAI API returned non-2xx status: {}", response.getStatusCode());
                return "Sorry, I encountered an issue. Please try again.";
            }
            
        } catch (RestClientException e) {
            log.error("Error calling OpenAI API: {}", e.getMessage(), e);
            return "Sorry, I'm having trouble connecting to my brain. Please try again later.";
        } catch (Exception e) {
            log.error("Unexpected error in chat: {}", e.getMessage(), e);
            return "Oops! Something went wrong. Please try again.";
        }
    }
    
    @Override
    public boolean isReady() {
        return apiKey != null && !apiKey.isEmpty() && !apiKey.equals("your-api-key-here");
    }
    
    /**
     * Get system prompt based on language for personality
     */
    private String getSystemPrompt(String language) {
        Map<String, String> prompts = new HashMap<>();
        
        prompts.put("en", "You are Satu, a friendly and helpful AI assistant. " +
            "Your personality is warm, casual, and fun - like a best friend. " +
            "You speak naturally and use appropriate emojis when suitable. " +
            "You're knowledgeable, respectful, and always try to help the user. " +
            "Keep responses concise but engaging.");
        
        prompts.put("hi", "आप सातु हैं, एक दोस्ताना और सहायक एआई सहायक। " +
            "आपका व्यक्तित्व गर्मजोशी भरा, आकस्मिक और मजेदार है - जैसे एक सबसे अच्छा दोस्त। " +
            "आप स्वाभाविक रूप से बोलते हैं और जहां उपयुक्त हो तो उपयुक्त इमोजी का उपयोग करते हैं। " +
            "आप ज्ञानी, सम्मानजनक हैं और हमेशा उपयोगकर्ता की मदद करने का प्रयास करते हैं। " +
            "जवाब संक्षिप्त लेकिन आकर्षक रखें।");
        
        prompts.put("mr", "तुम साटू आहात, एक मैत्रीपूर्ण आणि सहायक एआই सहायक। " +
            "तुमचे व्यक्तिमत्व उष्ण, आनंदी आणि मजेदार आहे - सर्वोत्तम मित्राप्रमाणे। " +
            "तुम नैसर्गिकरीत्या बोलता आणि जेथे योग्य असेल तेथे उपयुक्त इमोजी वापरता। " +
            "तुम ज्ञानी, सम्मानजनक आहात आणि सदा वापरकर्त्याला मदत करण्याचा प्रयत्न करता। " +
            "उत्तरे संक्षिप्त परंतु आकर्षक ठेवा।");
        
        return prompts.getOrDefault(language, prompts.get("en"));
    }
}

