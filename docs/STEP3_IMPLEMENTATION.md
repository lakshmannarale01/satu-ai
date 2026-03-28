# Step 3: Voice Support - Implementation Guide

## 📅 Status: In Progress (March 28, 2026)
## 🎯 Goal: Add Speech-to-Text and Text-to-Speech Capabilities

---

## Overview

Step 3 adds voice capabilities to the Satu AI chat system, enabling users to:
- 🎤 **Speak to Satu**: Use speech-to-text to send voice messages
- 🔊 **Hear Responses**: Get text-to-speech audio responses
- 🌐 **Multilingual Voice**: Support English, Hindi, and Marathi voice
- 📱 **Browser Native**: Leverage Web Speech API for fallback
- 🎯 **Persistent Voice**: Store voice messages in database

---

## Architecture Changes

### Backend Enhancements

#### 1. Database Schema Updates
**New Fields in `Message` entity:**
```
- messageType: ENUM [TEXT, VOICE, AUDIO] (default: TEXT)
- audioUrl: String (path to stored audio file)
- transcription: String (STT result)
- voiceCharacter: String (which voice was used for TTS)
- audioFilePath: String (local file path for audio storage)
- duration: Long (duration in milliseconds)
```

#### 2. New Service Layer
- **VoiceService Interface**: Abstract voice operations
- **OpenAIVoiceService**: OpenAI Whisper (STT) + TTS API integration
- **AudioStorageService**: Handle audio file management

#### 3. New REST Endpoints
```
POST   /api/voice/transcribe      - Convert speech to text (Whisper API)
POST   /api/voice/synthesize      - Convert text to speech (TTS API)
POST   /api/voice/upload          - Store audio file
GET    /api/voice/stream/{id}     - Stream audio file
DELETE /api/voice/{messageId}     - Delete voice message
```

#### 4. New DTOs
```
VoiceTranscribeRequest
VoiceTranscribeResponse
VoiceSynthesizeRequest
VoiceSynthesizeResponse
VoiceMessageDTO
```

### Frontend Enhancements

#### 1. New React Components
```
VoiceInput.js         - Microphone recording interface
VoiceOutput.js        - Audio playback controls
RecordingIndicator.js - Recording status visual
WaveformVisualizer.js - Real-time audio visualization
```

#### 2. React Hooks
```
useVoiceRecording()   - Manage recording state
useWebSpeech()        - Browser Web Speech API integration
useAudioPlayback()    - Audio playback state
useAudioContext()     - Web Audio API for visualization
```

#### 3. UI/UX Updates
- Microphone button in chat input area
- Recording indicator with timer
- Visual waveform during recording
- Voice message bubbles with playback controls
- Audio progress bar
- Language-specific voice selection

---

## Implementation Plan (12 Phases)

### Phase 1: Database Schema Extension
**Files to Modify:**
- `backend/src/main/java/com/satruai/model/Message.java`
- Create migration script: `backend/src/main/resources/db/migration/`

**Tasks:**
1. Add new fields to Message entity
2. Create migration for H2 database
3. Update MessageRepository with voice queries
4. Test backward compatibility

### Phase 2: Backend Voice Service Architecture
**Files to Create:**
- `backend/src/main/java/com/satruai/service/VoiceService.java` (Interface)
- `backend/src/main/java/com/satruai/service/impl/OpenAIVoiceService.java`
- `backend/src/main/java/com/satruai/service/AudioStorageService.java`

**Tasks:**
1. Define VoiceService interface with methods:
   - `transcribeAudio(byte[] audio, String language)`
   - `synthesizeSpeech(String text, String language, String voice)`
   - `validateAudioFile(MultipartFile file)`
2. Implement OpenAI Whisper integration
3. Implement OpenAI TTS integration
4. Add audio storage logic

### Phase 3: Audio Storage Configuration
**Files to Create:**
- `backend/src/main/java/com/satruai/config/AudioStorageConfig.java`
- `backend/audio/` directory (local storage)

**Tasks:**
1. Configure multipart file upload settings
2. Set up audio storage directory structure
3. Implement file validation (format, size, duration)
4. Add file cleanup utilities

### Phase 4: Voice REST API Implementation
**Files to Create:**
- `backend/src/main/java/com/satruai/controller/VoiceController.java`

**Files to Modify:**
- `backend/src/main/java/com/satruai/service/ChatService.java` (integrate voice)

**Tasks:**
1. Create VoiceController with endpoints
2. Implement request validation
3. Add error handling
4. Integrate with existing ChatService
5. Update ChatController to handle voice messages

### Phase 5: Frontend Voice Recording Hook
**Files to Create:**
- `frontend/src/hooks/useVoiceRecording.js`
- `frontend/src/hooks/useWebSpeech.js`

**Tasks:**
1. Create useVoiceRecording hook using Web Audio API
2. Implement browser permission handling
3. Create useWebSpeech fallback hook
4. Add audio context state management

### Phase 6: Voice Input Component
**Files to Create:**
- `frontend/src/components/VoiceInput.js`
- `frontend/src/components/VoiceInput.css`
- `frontend/src/components/RecordingIndicator.js`
- `frontend/src/components/WaveformVisualizer.js`

**Tasks:**
1. Create microphone button component
2. Implement recording UI with states:
   - Idle (ready to record)
   - Recording (active)
   - Processing (sending to server)
   - Done (sent)
3. Add visual feedback (waveform, timer)
4. Handle browser permissions gracefully

### Phase 7: Voice Output Component
**Files to Create:**
- `frontend/src/components/VoiceOutput.js`
- `frontend/src/components/VoiceOutput.css`
- `frontend/src/components/AudioPlayer.js`

**Tasks:**
1. Create audio playback component
2. Implement playback controls (play, pause, speed)
3. Add progress bar
4. Handle audio element lifecycle
5. Responsive design for different message types

### Phase 8: ChatWindow Integration
**Files to Modify:**
- `frontend/src/components/ChatWindow.js`
- `frontend/src/components/ChatWindow.css`
- `frontend/src/App.js`

**Tasks:**
1. Integrate VoiceInput into message input area
2. Update message rendering for voice messages
3. Add voice message display with audio player
4. Update language switching to affect voice language
5. Add loading states for audio processing

### Phase 9: Error Handling & Fallbacks
**Files to Create:**
- `frontend/src/utils/voiceDetection.js` (browser capability detection)
- `frontend/src/utils/voiceErrorHandler.js`

**Files to Modify:**
- `frontend/src/components/VoiceInput.js`
- `backend/src/main/java/com/satruai/controller/VoiceController.java`

**Tasks:**
1. Detect browser Web Speech API support
2. Implement graceful fallbacks for unsupported browsers
3. Add retry logic for API failures
4. Handle network timeouts
5. Provide user-friendly error messages

### Phase 10: Multilingual Voice Support
**Files to Modify:**
- `backend/src/main/java/com/satruai/service/impl/OpenAIVoiceService.java`
- `frontend/src/hooks/useWebSpeech.js`
- `frontend/src/components/VoiceInput.js`

**Tasks:**
1. Map language codes to OpenAI Whisper language parameters
2. Map language codes to OpenAI TTS supported voices
3. Test multilingual audio recognition
4. Update language selector to include voice language

### Phase 11: Testing & Validation
**Files to Create:**
- `backend/src/test/java/com/satruai/service/VoiceServiceTest.java`
- `backend/src/test/java/com/satruai/controller/VoiceControllerTest.java`
- `frontend/src/components/__tests__/VoiceInput.test.js`
- `frontend/src/hooks/__tests__/useVoiceRecording.test.js`

**Tasks:**
1. Unit tests for VoiceService methods
2. Integration tests for voice endpoints
3. React component tests
4. Hook testing with mock Web Audio API
5. End-to-end voice flow testing

### Phase 12: Documentation & Release
**Files to Create:**
- `STEP3_VOICE_FEATURES.md` (User guide)
- Update `SATU_AI_PROJECT.md` with Step 3 status

**Tasks:**
1. Document all new endpoints
2. Create browser compatibility matrix
3. Write troubleshooting guide
4. Update project status
5. Prepare for Step 4

---

## API Specification

### 1. Transcribe Audio (Speech-to-Text)
```http
POST /api/voice/transcribe
Content-Type: multipart/form-data

Parameters:
- audio: (file, required) WAV/MP3/M4A format, max 25MB
- language: (string, optional) "en", "hi", "mr" (default: "en")
- sessionId: (string, required) Conversation session ID

Response (200 OK):
{
  "text": "Hello Satu",
  "language": "en",
  "confidence": 0.95,
  "duration": 2500,
  "messageId": 123
}

Response (400 Bad Request):
{
  "error": "Audio file too large",
  "maxSize": "25MB"
}
```

### 2. Synthesize Speech (Text-to-Speech)
```http
POST /api/voice/synthesize
Content-Type: application/json

Body:
{
  "text": "How can I help you today?",
  "language": "en",
  "voice": "nova",
  "speed": 1.0
}

Response (200 OK):
{
  "audioUrl": "/api/voice/stream/124",
  "contentType": "audio/mpeg",
  "duration": 3200,
  "messageId": 124
}
```

### 3. Upload Audio File
```http
POST /api/voice/upload
Content-Type: multipart/form-data

Parameters:
- audio: (file, required)
- sessionId: (string, required)

Response (200 OK):
{
  "fileId": "audio_1234567890",
  "fileSize": 45678,
  "duration": 5000,
  "url": "/api/voice/stream/audio_1234567890"
}
```

### 4. Stream Audio File
```http
GET /api/voice/stream/{messageId}

Response (200 OK):
- Content-Type: audio/mpeg
- Binary audio data
```

### 5. Delete Voice Message
```http
DELETE /api/voice/{messageId}

Response (204 No Content):
(Empty response)

Response (404 Not Found):
{
  "error": "Message not found"
}
```

---

## Frontend Component API

### VoiceInput Component
```jsx
<VoiceInput 
  onRecordingComplete={(audioBlob) => { }}
  onTranscription={(text) => { }}
  language="en"
  disabled={false}
  maxDuration={60000}
/>
```

### VoiceOutput Component
```jsx
<VoiceOutput 
  audioUrl="/api/voice/stream/123"
  autoplay={false}
  onPlayStart={() => { }}
  onPlayEnd={() => { }}
/>
```

### useVoiceRecording Hook
```jsx
const {
  isRecording,
  audioBlob,
  error,
  startRecording,
  stopRecording,
  cancelRecording,
  waveformData
} = useVoiceRecording();
```

---

## Environment Configuration

### Backend (application.properties)
```properties
# Voice Service Configuration
voice.provider=openai
voice.api.key=${LLM_API_KEY}
voice.whisper.model=whisper-1
voice.tts.model=tts-1
voice.tts.voice=nova

# Audio Storage
voice.storage.path=${user.home}/satu-ai/audio
voice.storage.max-file-size=25MB
voice.storage.cleanup-enabled=true
voice.storage.retention-days=30

# Audio Format
voice.audio.format=mp3
voice.audio.sample-rate=16000
voice.audio.bit-depth=16

# Performance
voice.api.timeout=30s
voice.api.max-retries=3
```

### Frontend (.env)
```
REACT_APP_VOICE_ENABLED=true
REACT_APP_VOICE_STORAGE_PATH=/api/voice
REACT_APP_MAX_RECORDING_DURATION=60000
REACT_APP_AUDIO_CHUNK_SIZE=1024
```

---

## Browser Compatibility

| Browser | Web Speech API | Web Audio API | Support |
|---------|----------------|---------------|---------|
| Chrome 90+ | ✅ Yes | ✅ Yes | ✅ Full |
| Edge 90+ | ✅ Yes | ✅ Yes | ✅ Full |
| Firefox 79+ | ❌ No | ✅ Yes | ⚠️ Partial* |
| Safari 14+ | ✅ Yes* | ✅ Yes | ⚠️ Partial* |
| Mobile Chrome | ✅ Yes | ✅ Yes | ✅ Full |
| Mobile Safari | ✅ Yes* | ✅ Yes | ✅ Full |

*Use OpenAI API as fallback or handle gracefully

---

## Troubleshooting Guide

### Microphone Not Detecting
**Solution:**
1. Check browser permissions: Settings → Privacy → Microphone
2. Reload page and grant permission
3. Test with: https://www.mediacapture.app/

### Audio Not Playing
**Solution:**
1. Check browser autoplay policy: Requires user interaction first
2. Update browser to latest version
3. Check browser console for CORS errors

### Transcription Showing Wrong Language
**Solution:**
1. Verify OpenAI Whisper language parameter
2. Check audio language matches selected language
3. Try speaking more clearly

### Voice Synthesis Not Working
**Solution:**
1. Verify OpenAI API key has TTS permissions
2. Check text length (max varies by provider)
3. Verify language/voice combination is valid

### Permission Denied Error
**Solution:**
1. Check microphone is connected and not in use
2. Reset browser audio permissions
3. Try a different browser or private mode

---

## Next Steps (After Voice Support)

**Step 4: Task Execution System**
- PC command execution with safety
- Permission system
- Command history and logging

**Step 5: Screen Monitoring**
- Screen capture and analysis
- OCR integration
- Activity detection

---

## Resources

### OpenAI Voice APIs
- [Whisper API (STT)](https://platform.openai.com/docs/guides/speech-to-text)
- [TTS API](https://platform.openai.com/docs/guides/text-to-speech)

### Web APIs
- [Web Audio API](https://developer.mozilla.org/en-US/docs/Web/API/Web_Audio_API)
- [Web Speech API](https://developer.mozilla.org/en-US/docs/Web/API/Web_Speech_API)
- [MediaRecorder API](https://developer.mozilla.org/en-US/docs/Web/API/MediaRecorder)

### React Patterns
- [Using the Effect Hook](https://react.dev/reference/react/useEffect)
- [Custom Hooks](https://react.dev/learn/reusing-logic-with-custom-hooks)

---

**Status:** Planning & Design Complete | Next: Phase 1 Implementation

