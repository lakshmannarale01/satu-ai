package com.satruai.service.impl;

import com.satruai.service.LLMService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Demo LLM Service - Works without API key for testing
 * Provides intelligent mock responses for demo purposes
 */
@Service
@Slf4j
public class DemoLLMService implements LLMService {
    
    private final Random random = new Random();
    private final Map<String, String[]> demoResponses = new HashMap<>();
    
    public DemoLLMService() {
        initializeDemoResponses();
    }
    
    private void initializeDemoResponses() {
        // English responses
        demoResponses.put("en_greeting", new String[]{
            "👋 Hello! I'm Satu, your AI assistant. How can I help you today?",
            "Hi there! 😊 What would you like to chat about?",
            "Hey! I'm here and ready to help. What's on your mind?"
        });
        
        demoResponses.put("en_question", new String[]{
            "That's a great question! Let me help you with that. Based on common knowledge, this is typically handled by considering various factors and perspectives.",
            "Interesting! I can help you explore that topic. There are several aspects to consider here.",
            "Good thinking! This relates to many important concepts. Let me break it down for you."
        });
        
        demoResponses.put("en_default", new String[]{
            "I understand what you mean. That's definitely worth exploring further!",
            "Great point! I appreciate you sharing that with me.",
            "That's interesting! Tell me more about what you're thinking.",
            "I see! That's a valuable perspective. How can I assist further?"
        });
        
        // Hindi responses
        demoResponses.put("hi_greeting", new String[]{
            "👋 नमस्ते! मैं Satu हूँ, आपका AI सहायक। मैं आपकी कैसे मदद कर सकता हूँ?",
            "नमस्ते! 😊 आप आज क्या पूछना चाहते हैं?",
            "हैलो! मैं आपकी मदद के लिए यहाँ हूँ। बताइए, क्या बात है?"
        });
        
        demoResponses.put("hi_question", new String[]{
            "बहुत अच्छा सवाल है! इस विषय में कई महत्वपूर्ण पहलू हैं।",
            "दिलचस्प! इस बारे में और जानकारी के लिए हम कुछ बातों पर विचार कर सकते हैं।",
            "बिल्कुल! यह एक महत्वपूर्ण विषय है। आइए इसे विस्तार से समझते हैं।"
        });
        
        demoResponses.put("hi_default", new String[]{
            "मैं समझ गया। यह बिल्कुल सही है!",
            "बहुत अच्छी बात है! आपका दृष्टिकोण सराहनीय है।",
            "दिलचस्प! कृपया और बताइए।",
            "मुझे यह पसंद आया। मैं कैसे और मदद कर सकता हूँ?"
        });
        
        // Marathi responses
        demoResponses.put("mr_greeting", new String[]{
            "👋 नमस्कार! मी Satu आहे, तुमचा AI सहायक. मी तुम्हाला कसे मदत करू शकतो?",
            "नमस्कार! 😊 आज तुम्हाला काय विचारायचे आहे?",
            "हाय! मी तुम्हाला मदत करायला तयार आहे. बरं, सांग?"
        });
        
        demoResponses.put("mr_question", new String[]{
            "खूप चांगला प्रश्न! या विषयावर अनेक महत्वाचे पहलू आहेत.",
            "मनोरंजक! या बद्दल अधिक माहिती देण्यासाठी काही गोष्टी विचारात घेऊ शकतो.",
            "अगदी सरिसृप! हे एक महत्वाचे विषय आहे. चला याला तपशीलवारपणे समजून घेऊ."
        });
        
        demoResponses.put("mr_default", new String[]{
            "मला समजले. हे अगदी बरोबर आहे!",
            "खूप चांगली बाब आहे! तुमचा दृष्टिकोन प्रशंसनीय आहे.",
            "मनोरंजक! कृपया आणखी सांग.",
            "मला हे आवडले. मी आणखी कसे मदत करू शकतो?"
        });
    }
    
    @Override
    public String chat(String userMessage, String language) {
        log.info("Demo mode - Processing message: {} in language: {}", userMessage, language);
        
        try {
            // Determine language code
            String langCode = language != null ? language.toLowerCase() : "en";
            
            // Detect message type
            String responseType = detectMessageType(userMessage);
            String key = langCode + "_" + responseType;
            
            // Get response array
            String[] responses = demoResponses.getOrDefault(key, 
                demoResponses.getOrDefault(langCode + "_default", 
                demoResponses.get("en_default")));
            
            // Return random response
            String response = responses[random.nextInt(responses.length)];
            
            // Simulate thinking time
            Thread.sleep(500 + random.nextInt(1000));
            
            log.info("Demo response generated for language: {}", langCode);
            return response;
            
        } catch (InterruptedException e) {
            log.error("Error in demo LLM service", e);
            Thread.currentThread().interrupt();
            return "I'm thinking... 🤔 Please ask me again!";
        }
    }
    
    @Override
    public boolean isReady() {
        // Demo mode is always ready!
        return true;
    }
    
    /**
     * Detect the type of message to provide appropriate response
     */
    private String detectMessageType(String message) {
        if (message == null || message.isEmpty()) {
            return "default";
        }
        
        String lowerMessage = message.toLowerCase().trim();
        
        // Greeting detection
        if (lowerMessage.matches(".*(hello|hi|hey|नमस्ते|नमस्कार|हाय|सलाम).*")) {
            return "greeting";
        }
        
        // Question detection
        if (lowerMessage.contains("?") || 
            lowerMessage.matches(".*(what|why|how|when|where|which|क्या|क्यों|कैसे|काय|कोण).*")) {
            return "question";
        }
        
        // Default
        return "default";
    }
}

