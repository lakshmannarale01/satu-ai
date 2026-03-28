# Quick Start Guide - Satu AI Chat System

## Prerequisites
- Java 11+ (OpenJDK)
- Node.js & npm
- Git
- OpenAI API key (optional for testing without real AI)

## Setup Instructions

### 1. Backend Setup

```bash
cd backend

# Compile
mvnw clean compile

# Run
mvnw spring-boot:run
```

**Backend runs on:** `http://localhost:8080`

**Database Console:** `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:satudb`
- Username: `sa`
- Password: (leave empty)

### 2. Frontend Setup

```bash
cd frontend

# Install dependencies
npm install

# Start development server
npm start
```

**Frontend opens on:** `http://localhost:3000`

## Features

### Chat Features
- 💬 Real-time chat with AI
- 🌐 Multilingual support (English, Hindi, Marathi)
- 💾 Conversation history persistence
- ⚡ Typing indicators
- 🎨 Beautiful gradient UI
- ❌ Error handling and recovery

### Multilingual Prompts
The AI responds in the selected language with appropriate personality:

**English:** Friendly and casual
**Hindi:** दोस्ताना (Friendly tone)
**Marathi:** मैत्रीपूर्ण (Friendly manner)

## Environment Variables

### To enable real OpenAI responses:

#### Windows PowerShell
```powershell
$env:LLM_API_KEY = "sk-your-api-key-here"
```

#### Windows Command Prompt
```cmd
set LLM_API_KEY=sk-your-api-key-here
```

#### Linux/Mac
```bash
export LLM_API_KEY="sk-your-api-key-here"
```

Then restart the backend server.

## Default Behavior Without API Key

Without an OpenAI API key, the chat system will:
- Show a fallback message: "I'm currently not available. Please check back later."
- Demonstrate the UI and data flow
- Allow you to test the interface

## API Endpoints

### Create New Conversation
```
GET /api/chat/new?language=en
```

### Send Message
```
POST /api/chat/send
Content-Type: application/json

{
  "sessionId": "uuid-here",
  "message": "Hello Satu!",
  "language": "en"
}
```

### Get Conversation History
```
GET /api/chat/history/{sessionId}
```

### Health Check
```
GET /api/chat/health
```

## Testing with cURL

### Create Session
```bash
curl http://localhost:8080/api/chat/new?language=en
```

### Send Message
```bash
curl -X POST http://localhost:8080/api/chat/send \
  -H "Content-Type: application/json" \
  -d '{
    "sessionId": "your-session-id",
    "message": "Hello!",
    "language": "en"
  }'
```

## Troubleshooting

### Frontend can't connect to backend
- Verify backend is running on port 8080
- Check CORS configuration in `SecurityConfig.java`
- Clear browser cache and refresh

### Database errors
- H2 database is in-memory (data resets on restart)
- Check `application.properties` for database configuration

### AI not responding
- Verify `LLM_API_KEY` environment variable is set
- Check API key has credits and correct permissions
- Monitor backend logs for OpenAI API errors

### Port already in use
```bash
# Change backend port in application.properties
server.port=8081

# Change frontend port (set PORT environment variable)
PORT=3001 npm start
```

## Database Structure

### Conversations Table
```sql
CREATE TABLE conversations (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  session_id VARCHAR(255) UNIQUE NOT NULL,
  language VARCHAR(10),
  title VARCHAR(255),
  created_at TIMESTAMP,
  last_updated_at TIMESTAMP,
  is_active BOOLEAN
);
```

### Messages Table
```sql
CREATE TABLE messages (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  conversation_id BIGINT NOT NULL,
  sender VARCHAR(50),
  content LONGTEXT,
  language VARCHAR(10),
  timestamp TIMESTAMP,
  ai_model VARCHAR(100),
  response_time BIGINT,
  FOREIGN KEY (conversation_id) REFERENCES conversations(id)
);
```

## Development Tips

### Enable Debug Logging
Edit `application.properties`:
```properties
logging.level.com.satruai=DEBUG
```

### Monitor API Calls
- Open browser DevTools (F12)
- Go to Network tab
- Watch requests/responses in real-time

### Database Inspection
1. Visit `http://localhost:8080/h2-console`
2. Click "Connect"
3. Run queries:
   ```sql
   SELECT * FROM conversations;
   SELECT * FROM messages;
   ```

## Next Steps

1. Add an OpenAI API key for real AI conversations
2. Test multilingual support (Hindi/Marathi)
3. Review logs and optimize performance
4. Proceed to Step 3: Voice Support

## Support

For issues or questions:
1. Check the logs in terminal
2. Review STEP2_IMPLEMENTATION.md for architecture details
3. Verify all prerequisites are installed
4. Ensure ports 8080 and 3000 are available

---

**Happy Chatting with Satu AI! 🚀**

