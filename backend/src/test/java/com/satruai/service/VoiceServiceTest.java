package com.satruai.service;

import com.satruai.service.impl.OpenAIVoiceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for OpenAI Voice Service
 */
@DisplayName("OpenAI Voice Service Tests")
class VoiceServiceTest {

    private OpenAIVoiceService voiceService;
    private MultipartFile mockAudioFile;

    @BeforeEach
    void setUp() {
        voiceService = new OpenAIVoiceService();
        mockAudioFile = mock(MultipartFile.class);
    }

    @Test
    @DisplayName("Should validate supported audio formats")
    void testValidateAudioFileFormat() {
        // MP3 file
        when(mockAudioFile.getContentType()).thenReturn("audio/mpeg");
        when(mockAudioFile.getOriginalFilename()).thenReturn("test.mp3");
        when(mockAudioFile.isEmpty()).thenReturn(false);
        when(mockAudioFile.getSize()).thenReturn(1000000L);
        
        assertTrue(voiceService.validateAudioFile(mockAudioFile));
    }

    @Test
    @DisplayName("Should reject empty audio files")
    void testValidateEmptyAudioFile() {
        when(mockAudioFile.isEmpty()).thenReturn(true);
        
        assertFalse(voiceService.validateAudioFile(mockAudioFile));
    }

    @Test
    @DisplayName("Should reject files exceeding max size")
    void testValidateAudioFileSize() {
        // 26MB file (exceeds 25MB limit)
        when(mockAudioFile.isEmpty()).thenReturn(false);
        when(mockAudioFile.getSize()).thenReturn(27262976L);
        when(mockAudioFile.getContentType()).thenReturn("audio/mpeg");
        when(mockAudioFile.getOriginalFilename()).thenReturn("test.mp3");
        
        assertFalse(voiceService.validateAudioFile(mockAudioFile));
    }

    @Test
    @DisplayName("Should reject unsupported audio formats")
    void testValidateUnsupportedFormat() {
        when(mockAudioFile.isEmpty()).thenReturn(false);
        when(mockAudioFile.getSize()).thenReturn(1000000L);
        when(mockAudioFile.getContentType()).thenReturn("audio/flac");
        when(mockAudioFile.getOriginalFilename()).thenReturn("test.flac");
        
        assertFalse(voiceService.validateAudioFile(mockAudioFile));
    }

    @Test
    @DisplayName("Should return supported voices for language")
    void testGetSupportedVoices() {
        String[] voices = voiceService.getSupportedVoices("en");
        
        assertNotNull(voices);
        assertTrue(voices.length > 0);
        assertTrue(java.util.Arrays.asList(voices).contains("nova"));
    }

    @Test
    @DisplayName("Should be ready when API key is configured")
    void testIsReadyWithValidApiKey() {
        // Set a valid API key
        ReflectionTestUtils.setField(voiceService, "apiKey", "sk-test-key");
        
        assertTrue(voiceService.isReady());
    }

    @Test
    @DisplayName("Should not be ready when API key is missing")
    void testIsReadyWithoutApiKey() {
        // Set empty API key
        ReflectionTestUtils.setField(voiceService, "apiKey", "");
        
        assertFalse(voiceService.isReady());
    }

    @Test
    @DisplayName("Should handle null API key")
    void testIsReadyWithNullApiKey() {
        // Set null API key
        ReflectionTestUtils.setField(voiceService, "apiKey", null);
        
        assertFalse(voiceService.isReady());
    }
}

