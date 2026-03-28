# Satu AI - Backend Documentation (as of 2026-03-28)

---

## 1. Overview
- **Framework:** Spring Boot (Java)
- **Key Features:**
  - Modular LLM support (OpenAI, Hugging Face, Demo)
  - Conversation/message persistence (H2 DB)
  - REST API for chat, history, and voice (planned)
  - Voice support planned (Google Cloud, OpenAI, or local)

---

## 2. LLM Providers
- **DemoLLMService:** No API key, generic responses
- **HuggingFaceLLMService:** Real AI, free tier, set `HF_API_KEY`
- **OpenAILLMService:** Premium, set `LLM_API_KEY`
- **LLMConfig:** Auto-selects provider based on available keys

---

## 3. Voice Support (Planned)
- **Current:** Not yet implemented
- **Planned:** Google Cloud STT/TTS (free tier), OpenAI, or local
- **Config:**
  - `voice.provider=google`
  - `GOOGLE_APPLICATION_CREDENTIALS` env var for service account

---

## 4. Key Files
- `src/main/java/com/satruai/service/impl/DemoLLMService.java`
- `src/main/java/com/satruai/service/impl/HuggingFaceLLMService.java`
- `src/main/java/com/satruai/service/impl/OpenAILLMService.java`
- `src/main/java/com/satruai/config/LLMConfig.java`
- `src/main/resources/application.properties`

---

## 5. API Keys & Configuration
- **Hugging Face:**
  - Set `HF_API_KEY` env var
- **OpenAI:**
  - Set `LLM_API_KEY` env var
- **Google Cloud (voice):**
  - Set `GOOGLE_APPLICATION_CREDENTIALS` env var (JSON file)

---

## 6. How to Start Backend
```bash
cd backend
mvnw spring-boot:run
```

---

## 7. Outstanding TODOs
- [ ] Implement `GoogleVoiceService.java` for STT/TTS
- [ ] Add Google Cloud credentials
- [ ] Test voice features
- [ ] (Optional) Add OpenAI or other providers
- [ ] (Optional) Add more advanced features (external data, etc.)

---

## 8. How to Resume
- To continue, just run:
  ```
  read docs/BACKEND_SNAPSHOT.md
  ```
- I will pick up exactly where we left off!

---

**Snapshot saved: March 28, 2026**

You can now safely stop and resume later from this exact point!
