# Running Satu AI - Step 3 with Voice Support

## ⚡ Quick Start (5 minutes)

### Prerequisites
- Java 11+ (check: `java -version`)
- Node.js 14+ (check: `node -v`)
- OpenAI API key (get from https://platform.openai.com/api-keys)
- Working microphone

### Setup

#### 1. Set Environment Variable (Required)
**Windows PowerShell**:
```powershell
$env:LLM_API_KEY = "sk-your-actual-api-key-here"
```

**Windows Command Prompt**:
```cmd
set LLM_API_KEY=sk-your-actual-api-key-here
```

**Linux/Mac**:
```bash
export LLM_API_KEY="sk-your-actual-api-key-here"
```

#### 2. Start Backend
```bash
cd backend
mvnw spring-boot:run
```

Wait for: `Started SatuAiApplication in X seconds`

#### 3. Start Frontend (New Terminal)
```bash
cd frontend
npm install  # Only first time
npm start
```

Wait for: Browser opens to `http://localhost:3000`

### That's It! 🎉
- Open http://localhost:3000 in browser
- Click microphone button 🎤
- Grant permission
- Speak: "Hello Satu"
- See transcription
- Hear AI response

---

## 🔧 Full Setup Guide

### 1. Backend Setup

#### Step 1a: Navigate to Backend
```bash
cd backend
```

#### Step 1b: Configure Environment
The application looks for the API key in this order:
1. `LLM_API_KEY` environment variable
2. `application.properties` (not recommended for production)
3. System properties

**Set the environment variable**:
```bash
# Windows PowerShell
$env:LLM_API_KEY = "sk-..."

# Linux/Mac
export LLM_API_KEY="sk-..."
```

#### Step 1c: Compile (optional)
```bash
mvnw clean compile
```

#### Step 1d: Run Backend
```bash
mvnw spring-boot:run
```

**Expected Output**:
```
2026-03-28 12:00:00.000  INFO ... : Starting SatuAiApplication v...
2026-03-28 12:00:05.123  INFO ... : Started SatuAiApplication in 5.123 seconds (JVM running for 6.789)
```

**Backend URL**: http://localhost:8080

#### Step 1e: Verify Backend
```bash
curl http://localhost:8080/api/chat/health
```

Should return: `{"status":"healthy"}`

### 2. Frontend Setup

#### Step 2a: Navigate to Frontend
```bash
cd frontend
```

#### Step 2b: Install Dependencies (First Time Only)
```bash
npm install
```

This installs React, Axios, and other dependencies.

#### Step 2c: Start Development Server
```bash
npm start
```

**Expected Output**:
```
Compiled successfully!

You can now view satu-ai in the browser.

  Local:            http://localhost:3000
  On Your Network:  http://192.168.x.x:3000
```

A browser window should automatically open.

### 3. Access the Application

**In Browser**:
- Frontend: http://localhost:3000
- Backend API: http://localhost:8080
- Database Console: http://localhost:8080/h2-console

**Database Credentials**:
- JDBC URL: `jdbc:h2:mem:satudb`
- Username: `sa`
- Password: (leave empty)

---

## 🎤 Using Voice Features

### Recording Voice
1. Click the **microphone button** (🎤) in the chat interface
2. Grant microphone permission if prompted
3. Speak clearly into the microphone
4. Speak: "Hello Satu" (or any message)
5. Click the button again to **stop recording**
6. Wait for transcription (2-5 seconds)

### Voice Transcript Appears
- You'll see your message transcribed in the chat
- It will show with a **"🎤 Voice Message"** badge
- The AI will respond to your message

### Hearing AI Response
- If the AI response has audio, an **audio player** will appear
- Click the **play button** (▶) to hear the response
- Use controls to:
  - Pause/Resume
  - Adjust speed (0.75x, 1x, 1.5x)
  - Download audio file
  - Seek to any point

### Multilingual Voice
1. Select language from dropdown: **English**, **हिंदी**, **मराठी**
2. Record voice in that language
3. AI will respond in the same language
4. Hear audio response in selected language

---

## 🐛 Troubleshooting

### Backend Won't Start

**Problem**: `Error: Port 8080 already in use`
```bash
# Kill existing process on port 8080
# Windows:
netstat -ano | findstr :8080
taskkill /PID <PID> /F

# Linux/Mac:
lsof -i :8080
kill -9 <PID>
```

**Problem**: `LLM_API_KEY environment variable not found`
```bash
# Verify variable is set:
echo $LLM_API_KEY  # Linux/Mac
echo %LLM_API_KEY%  # Windows

# If not set, set it again:
export LLM_API_KEY="sk-..."
```

**Problem**: `Unable to load class org.springframework.boot.loader.JarLauncher`
```bash
# Java version too old, need Java 11+
java -version
# Update Java if needed
```

### Frontend Won't Start

**Problem**: `Port 3000 already in use`
```bash
# Kill existing process
# Windows:
netstat -ano | findstr :3000
taskkill /PID <PID> /F

# Linux/Mac:
lsof -i :3000
kill -9 <PID>
```

**Problem**: `npm: command not found`
```bash
# Install Node.js from https://nodejs.org/
# Then verify:
npm -v
node -v
```

**Problem**: `Module not found: express` or other dependency
```bash
# Reinstall dependencies
rm -rf node_modules package-lock.json
npm install
npm start
```

### Voice Features Not Working

**Problem**: Microphone button disabled or greyed out
```
Solution:
1. Refresh the page (F5)
2. Check browser is Chrome, Edge, Firefox, or Safari
3. Verify microphone connected: Settings → Sound
```

**Problem**: "Voice service not configured" error
```
Solution:
1. Verify LLM_API_KEY environment variable is set
2. Restart backend: Ctrl+C and run again
3. Check backend log for "OpenAIVoiceService initialized"
```

**Problem**: Transcription shows "error" or doesn't complete
```
Solution:
1. Check internet connection
2. Verify OpenAI API key is valid
3. Check backend logs for error details
4. Try again with shorter audio
```

**Problem**: Audio playback doesn't work
```
Solution:
1. Unmute browser tab (right-click tab → unmute)
2. Check system volume is not muted
3. Clear browser cache (Ctrl+Shift+Del)
4. Try different browser
```

---

## 📊 Testing the Setup

### Quick Test
1. **Send Text Message**
   - Type: "Hello Satu"
   - Click: Send button
   - Should see: AI response

2. **Record Voice**
   - Click: Microphone button
   - Say: "What is the weather?"
   - Should see: Transcribed text + AI response

3. **Hear Response**
   - Send any message
   - AI responds with text (audio optional)
   - Look for audio player in response

### Full Health Check
```bash
# Check backend health
curl http://localhost:8080/api/chat/health
curl http://localhost:8080/api/voice/health

# Check database
curl http://localhost:8080/h2-console

# Check voice capabilities
curl http://localhost:8080/api/voice/voices/en
```

---

## 📈 Performance Tips

### Optimize Backend
```bash
# Run with more memory
mvnw spring-boot:run -Xmx512M
```

### Optimize Frontend
```bash
# Create production build (faster)
npm run build
```

### Check Resource Usage
- **Backend CPU**: Should be <20%
- **Backend Memory**: Should be <200MB
- **Frontend CPU**: Should be <10% idle

---

## 🔐 Security Notes

### API Key Safety
- **NEVER** commit API key to git
- **ALWAYS** use environment variables
- If leaked, regenerate at https://platform.openai.com/api-keys
- Rotate keys periodically

### Microphone Privacy
- Browser requests permission for microphone
- Audio only sent to OpenAI for transcription
- Audio files stored locally on server
- User can block microphone access at any time

### Default Localhost Only
- Frontend runs on `http://localhost:3000`
- Backend runs on `http://localhost:8080`
- Not accessible from external networks by default
- For remote access, configure firewall and HTTPS

---

## 📚 Additional Resources

### Documentation
- **STEP3_VOICE_FEATURES.md**: Voice features guide
- **STEP3_TESTING_GUIDE.md**: Comprehensive test cases
- **STEP3_IMPLEMENTATION.md**: Technical architecture
- **QUICKSTART.md**: Project quick start

### External Links
- [OpenAI API Docs](https://platform.openai.com/docs)
- [React Documentation](https://react.dev)
- [Spring Boot Guide](https://spring.io/guides/gs/spring-boot/)
- [Web Audio API](https://developer.mozilla.org/en-US/docs/Web/API/Web_Audio_API)

### Getting Help
1. Check backend logs: Console where `mvnw spring-boot:run` runs
2. Check frontend logs: Browser F12 → Console tab
3. Check database: http://localhost:8080/h2-console
4. Review troubleshooting sections in documentation

---

## 🚀 Next Steps

### After Setup Works
1. ✅ Test voice recording (record 3+ messages)
2. ✅ Test voice playback (play responses)
3. ✅ Test different languages (English, Hindi, Marathi)
4. ✅ Test mobile browser (if available)
5. ✅ Run test suite: See STEP3_TESTING_GUIDE.md

### Ready for Development
- Explore code in `backend/src` and `frontend/src`
- Make modifications and see changes automatically
- Backend: Changes require restart
- Frontend: Hot reload on save

### Deploy to Production
- Build backend: `mvnw clean package`
- Build frontend: `npm run build`
- Deploy both to server
- Update configuration for production environment

---

## 📞 Common Commands

```bash
# Backend
cd backend
mvnw clean compile      # Compile only
mvnw clean package      # Create JAR file
mvnw clean package -DskipTests  # Skip tests
mvnw spring-boot:run    # Run in development

# Frontend
cd frontend
npm install             # Install dependencies
npm start               # Start development server
npm run build           # Create production build
npm test                # Run tests
npm run eject           # Advanced: Eject from create-react-app

# Database
curl http://localhost:8080/h2-console  # Access H2 console
```

---

## 🎯 Verification Checklist

Before declaring setup complete:

- [ ] Backend running: `Started SatuAiApplication` in logs
- [ ] Frontend running: Browser opened to localhost:3000
- [ ] Can send text message and see AI response
- [ ] Microphone button visible and clickable
- [ ] Can record voice message (grant permission)
- [ ] Transcription appears after recording
- [ ] AI responds to voice message
- [ ] Database contains messages (check H2 console)
- [ ] No errors in browser console (F12)
- [ ] No errors in backend logs
- [ ] Voice health check passes: `curl http://localhost:8080/api/voice/health`

---

## 💡 Tips & Tricks

### Faster Development
```bash
# Terminal 1: Backend (stays running)
cd backend && mvnw spring-boot:run

# Terminal 2: Frontend (hot reload)
cd frontend && npm start

# Terminal 3: Use for commands
```

### Database Inspection
```bash
# Use H2 console for ad-hoc queries
# http://localhost:8080/h2-console
# Query: SELECT * FROM messages ORDER BY timestamp DESC;
```

### Voice Testing
```bash
# Test transcription via curl
curl -X POST http://localhost:8080/api/voice/transcribe \
  -F "audio=@recording.wav" \
  -F "language=en"

# Test voices available
curl http://localhost:8080/api/voice/voices/en
```

### Logs Inspection
```bash
# See what's happening
# Backend logs: Console where spring-boot:run runs
# Frontend logs: Browser F12 → Console
# Database: h2-console
```

---

**Happy coding! 🚀**

For detailed features, see: **STEP3_VOICE_FEATURES.md**  
For testing, see: **STEP3_TESTING_GUIDE.md**

