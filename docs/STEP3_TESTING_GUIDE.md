# Step 3 Voice Support - Testing Guide

## 🧪 Testing Overview

This document provides comprehensive testing procedures for Step 3 Voice Support features.

---

## ✅ Pre-Testing Checklist

### Environment Setup
- [ ] Backend running on `http://localhost:8080`
- [ ] Frontend running on `http://localhost:3000`
- [ ] OpenAI API key configured (`LLM_API_KEY` environment variable set)
- [ ] Microphone connected and working
- [ ] Network connection stable
- [ ] Browser updated to latest version

### Database
- [ ] H2 database initialized
- [ ] Audio storage directory exists: `~/satu-ai/audio/`
- [ ] Database has write permissions

---

## 🎤 Speech-to-Text (Transcription) Testing

### Test Case 1: Basic Voice Recording
**Objective**: Verify basic microphone recording works

**Steps**:
1. Open http://localhost:3000 in browser
2. Click microphone button (🎤)
3. Grant microphone permission if prompted
4. Speak clearly: "Hello Satu, how are you today?"
5. Stop recording (click button again)

**Expected Results**:
- [ ] Recording button shows "Recording..." status
- [ ] Waveform visualizes during recording
- [ ] Timer counts seconds elapsed
- [ ] Audio sent to server automatically
- [ ] Transcribed text appears in chat

**Evidence**: Screenshot of transcribed message

---

### Test Case 2: Multilingual Transcription
**Objective**: Verify transcription works for multiple languages

**Steps for Each Language**:

#### English
1. Select "English" from language dropdown
2. Click microphone and say: "I want to schedule a meeting tomorrow"
3. Verify transcription

#### Hindi
1. Select "हिंदी" from language dropdown
2. Click microphone and say (in Hindi): "मुझे कल की मीटिंग शेड्यूल करनी है"
3. Verify transcription

#### Marathi
1. Select "मराठी" from language dropdown
2. Click microphone and say (in Marathi): "मला उद्यापासून सुरुवात करायचे आहे"
3. Verify transcription

**Expected Results**:
- [ ] Each language transcribes correctly
- [ ] AI responses in same language
- [ ] No language mixing

**Evidence**: Transcriptions in three languages

---

### Test Case 3: Long Duration Recording
**Objective**: Test maximum recording duration (60 seconds)

**Steps**:
1. Click microphone button
2. Record approximately 45 seconds of continuous speech
3. Stop recording

**Expected Results**:
- [ ] Recording continues for full duration
- [ ] Waveform updates continuously
- [ ] Timer shows correct elapsed time
- [ ] Transcription completes successfully

**Evidence**: Recording of 45+ seconds with successful transcription

---

### Test Case 4: Error Handling - Permission Denied
**Objective**: Verify graceful error when microphone permission denied

**Steps**:
1. Deny microphone permission when prompted
2. Click microphone button
3. Observe error message

**Expected Results**:
- [ ] Error message appears
- [ ] Message is user-friendly and helpful
- [ ] No crash or console errors
- [ ] Can retry after granting permission

**Evidence**: Screenshot of error message

---

### Test Case 5: Network Error During Transcription
**Objective**: Test recovery from network errors

**Steps**:
1. Open DevTools (F12)
2. Go to Network tab
3. Enable "Offline" mode
4. Click microphone and record audio
5. Re-enable network before upload completes

**Expected Results**:
- [ ] Error message displays
- [ ] Retry button appears (if implemented)
- [ ] Audio not lost
- [ ] Can retry with network restored

**Evidence**: Console showing error handling

---

### Test Case 6: Rapid Successive Recordings
**Objective**: Test handling multiple recordings back-to-back

**Steps**:
1. Record message #1: "First test"
2. Stop and wait for response
3. Record message #2: "Second test"
4. Stop and wait for response
5. Record message #3: "Third test"

**Expected Results**:
- [ ] All three messages transcribed correctly
- [ ] AI responds to each message
- [ ] No message loss or duplication
- [ ] Chat history shows all messages in order

**Evidence**: Chat showing three sequential voice messages

---

## 🔊 Text-to-Speech (Synthesis) Testing

### Test Case 7: Basic Audio Playback
**Objective**: Verify audio can be played

**Steps**:
1. Send text message to AI: "Tell me about the weather"
2. Wait for AI response
3. If response has audio player, click play button
4. Listen to audio

**Expected Results**:
- [ ] Audio player appears in message
- [ ] Play button is clickable
- [ ] Audio plays without errors
- [ ] Sound is clear and audible
- [ ] UI updates during playback

**Evidence**: Screenshot of playing audio message

---

### Test Case 8: Playback Controls
**Objective**: Test all audio player controls

**Steps**:
1. Play an audio message
2. Pause after 2 seconds
3. Resume playback
4. Skip to 50% progress using progress bar
5. Change speed to 1.5x
6. Return to 1x speed

**Expected Results**:
- [ ] Pause stops playback immediately
- [ ] Resume continues from pause point
- [ ] Progress bar seeks correctly
- [ ] Speed controls work (audio faster/slower)
- [ ] Time display updates accurately

**Evidence**: Screen recording of control interactions

---

### Test Case 9: Download Audio
**Objective**: Verify audio download functionality

**Steps**:
1. Play an audio message
2. Click download button (⬇)
3. Check downloads folder

**Expected Results**:
- [ ] Download starts immediately
- [ ] File downloaded as `.mp3`
- [ ] File size > 0 bytes
- [ ] File is valid MP3 (can open in media player)

**Evidence**: Verify downloaded file can play

---

### Test Case 10: Multiple Voice Messages
**Objective**: Test switching between audio messages

**Steps**:
1. Send 3 text messages to get 3 different audio responses
2. Play first audio
3. While playing, start second audio
4. Switch to third audio

**Expected Results**:
- [ ] First audio stops when second starts
- [ ] No overlapping audio
- [ ] Each audio plays independently
- [ ] Progress tracking correct for each

**Evidence**: Chat with 3 audio messages, demonstration of switching

---

## 🎯 Integration Testing

### Test Case 11: Full Voice Conversation Flow
**Objective**: Test complete voice chat workflow

**Scenario**: User wants to schedule a meeting

**Steps**:
1. Open chat application
2. Click microphone: "Schedule a meeting for tomorrow at 2 PM"
3. Wait for transcription and AI response
4. AI responds with audio
5. Play audio response
6. Continue with voice: "Meeting with the sales team"
7. Wait for response
8. Play response

**Expected Results**:
- [ ] Transcription of first message accurate
- [ ] AI understands meeting context
- [ ] TTS response appropriate to request
- [ ] Second voice message recognized correctly
- [ ] Conversation is coherent
- [ ] Audio quality is consistent
- [ ] Chat history shows all messages

**Evidence**: Screen recording of complete conversation

---

### Test Case 12: Language Switching Mid-Conversation
**Objective**: Test switching languages during conversation

**Steps**:
1. Start conversation in English
2. Send voice message: "Hello, how are you?"
3. Get response in English
4. Switch to Hindi
5. Send voice message in Hindi
6. Verify response in Hindi

**Expected Results**:
- [ ] First response in English
- [ ] Language switch is smooth
- [ ] Hindi STT works correctly
- [ ] Hindi TTS works correctly
- [ ] No cross-language mixing

**Evidence**: Chat showing bilingual conversation

---

### Test Case 13: Voice Message Storage
**Objective**: Verify messages persisted in database

**Steps**:
1. Send 3 voice messages
2. Open database console: `http://localhost:8080/h2-console`
3. Query messages table:
   ```sql
   SELECT * FROM messages WHERE message_type = 'VOICE' ORDER BY timestamp DESC LIMIT 3;
   ```
4. Verify fields populated

**Expected Results**:
- [ ] 3 voice messages in database
- [ ] `message_type` = "VOICE"
- [ ] `transcription` contains transcribed text
- [ ] `audio_url` populated
- [ ] `audio_duration` > 0
- [ ] `timestamp` recent

**Evidence**: SQL query results showing voice messages

---

## 🌐 Browser Compatibility Testing

### Test Case 14: Chrome Browser
**Objective**: Full functionality in Chrome

**Steps**:
1. Open Chrome (version 90+)
2. Run all voice tests (Cases 1-13)

**Expected Results**:
- [ ] All voice features work
- [ ] No console errors
- [ ] Performance is smooth
- [ ] Waveform visualization smooth

---

### Test Case 15: Firefox Browser
**Objective**: Verify Firefox support with fallback

**Steps**:
1. Open Firefox (version 79+)
2. Test speech-to-text: Record voice
3. Test text-to-speech: Playback audio

**Expected Results**:
- [ ] Voice recording works (uses Web Audio API)
- [ ] Transcription works (uses OpenAI Whisper API)
- [ ] Audio playback works
- [ ] No critical errors

**Note**: Firefox doesn't have Web Speech API, but our implementation uses OpenAI API

---

### Test Case 16: Safari Browser
**Objective**: Verify Safari support

**Steps**:
1. Open Safari (version 14+)
2. Run voice tests
3. Check console for warnings

**Expected Results**:
- [ ] All voice features functional
- [ ] No critical errors
- [ ] Performance acceptable

---

### Test Case 17: Mobile Browser (Chrome)
**Objective**: Test voice on mobile device

**Steps**:
1. Open http://localhost:3000 on mobile phone via network IP
2. Test microphone recording
3. Test audio playback
4. Verify responsive layout

**Expected Results**:
- [ ] Voice button responsive to touch
- [ ] Microphone works on mobile
- [ ] Audio plays through speaker
- [ ] UI adapts to mobile screen
- [ ] No layout issues

---

## ⚠️ Error Scenario Testing

### Test Case 18: API Key Invalid
**Objective**: Verify handling of invalid API key

**Steps**:
1. Set `LLM_API_KEY` to invalid value: `invalid-key-123`
2. Restart backend
3. Try to record voice message

**Expected Results**:
- [ ] Error message: "Voice service not configured"
- [ ] No crash
- [ ] Backend logs error appropriately
- [ ] Service health check shows degraded

---

### Test Case 19: Audio File Too Large
**Objective**: Test file size validation

**Steps**:
1. Create a large audio file (>25MB)
2. Attempt to upload via curl or manual modification

**Expected Results**:
- [ ] Error message: "Audio file too large"
- [ ] File rejected before upload
- [ ] No server crash

---

### Test Case 20: Unsupported Audio Format
**Objective**: Test format validation

**Steps**:
1. Try uploading non-audio file (image, text)
2. Try uploading unsupported audio format

**Expected Results**:
- [ ] Error message: "Unsupported audio format"
- [ ] File rejected
- [ ] User informed of supported formats

---

## 📊 Performance Testing

### Test Case 21: Transcription Speed
**Objective**: Measure STT latency

**Procedure**:
1. Record 10-second audio
2. Note time when sent
3. Note time when transcription received
4. Calculate latency
5. Repeat 5 times, calculate average

**Expected Results**:
- [ ] Average latency: 2-5 seconds
- [ ] Consistent performance
- [ ] No timeout errors

**Evidence**: Latency measurements table

---

### Test Case 22: Synthesis Speed
**Objective**: Measure TTS latency

**Procedure**:
1. Send text message (20 words)
2. Note time when sent
3. Note time when audio received
4. Calculate latency
5. Repeat 5 times

**Expected Results**:
- [ ] Average latency: 1-3 seconds
- [ ] Audio quality consistent
- [ ] No quality degradation

---

### Test Case 23: Storage Usage
**Objective**: Monitor storage consumption

**Procedure**:
1. Record 20 voice messages
2. Check storage folder size:
   ```bash
   du -sh ~/satu-ai/audio/
   ```
3. Verify database storage

**Expected Results**:
- [ ] Audio files stored properly
- [ ] Total size reasonable (~1MB per minute)
- [ ] No duplicate files
- [ ] Cleanup script functional (if enabled)

---

## 📋 Test Results Summary

### Test Execution Checklist
```
Speech-to-Text Tests:
- [ ] Test 1: Basic Recording ___/10
- [ ] Test 2: Multilingual ___/10
- [ ] Test 3: Long Duration ___/10
- [ ] Test 4: Permission Error ___/10
- [ ] Test 5: Network Error ___/10
- [ ] Test 6: Rapid Recordings ___/10

Text-to-Speech Tests:
- [ ] Test 7: Basic Playback ___/10
- [ ] Test 8: Playback Controls ___/10
- [ ] Test 9: Download Audio ___/10
- [ ] Test 10: Multiple Messages ___/10

Integration Tests:
- [ ] Test 11: Full Flow ___/10
- [ ] Test 12: Language Switch ___/10
- [ ] Test 13: DB Storage ___/10

Browser Tests:
- [ ] Test 14: Chrome ___/10
- [ ] Test 15: Firefox ___/10
- [ ] Test 16: Safari ___/10
- [ ] Test 17: Mobile ___/10

Error Tests:
- [ ] Test 18: Invalid API Key ___/10
- [ ] Test 19: Large File ___/10
- [ ] Test 20: Bad Format ___/10

Performance Tests:
- [ ] Test 21: Transcription Speed ___/10
- [ ] Test 22: Synthesis Speed ___/10
- [ ] Test 23: Storage ___/10

OVERALL SCORE: ___/230 (Pass: >207/230)
```

---

## 🐛 Known Issues & Workarounds

### Issue: Microphone Permission Stuck
**Symptom**: Permission dialog doesn't appear or is stuck
**Workaround**:
1. Open browser settings
2. Find site permissions
3. Clear microphone permission for localhost
4. Reload page and try again

### Issue: Audio Playback Stutters
**Symptom**: Audio playback is choppy or stuttering
**Workaround**:
1. Close other browser tabs
2. Clear browser cache
3. Update browser to latest version
4. Try different browser

### Issue: Transcription Takes Too Long
**Symptom**: Transcription takes >10 seconds
**Workaround**:
1. Check internet connection speed
2. Verify API key usage limits
3. Check server logs for errors
4. Try shorter audio clips

---

## 📞 Support

For issues or questions:
1. Check logs: Backend console + Browser F12 console
2. Review troubleshooting in STEP3_VOICE_FEATURES.md
3. Check API status at https://status.openai.com
4. Verify configuration in application.properties

---

*Last Updated: March 28, 2026*

