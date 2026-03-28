# Satu AI - Frontend Documentation (as of 2026-03-28)

---

## 1. Overview
- **Framework:** React (JavaScript)
- **Key Features:**
  - Modern chat UI (App.js, ChatWindow.js)
  - Multilingual support (English, Hindi, Marathi)
  - Voice input/output UI (VoiceInput.js, VoiceOutput.js)
  - Language switching
  - Message history

---

## 2. Key Files
- `src/App.js` - Main app logic, session management
- `src/components/ChatWindow.js` - Chat UI, message handling, voice UI
- `src/components/VoiceInput.js` - Microphone/recording (UI ready, backend pending)
- `src/components/VoiceOutput.js` - Audio playback (UI ready, backend pending)
- `public/index.html` - App entry point
- `src/index.js` - React entry point

---

## 3. Voice Features
- **UI:** Microphone button, waveform, audio playback
- **Backend:** Not yet connected (planned: Google Cloud, OpenAI, or local)
- **Config:** No API key needed for UI; backend integration pending

---

## 4. How to Start Frontend
```bash
cd frontend
npm install
npm start
```

---

## 5. Outstanding TODOs
- [ ] Integrate backend voice API (Google, OpenAI, or local)
- [ ] Test end-to-end voice features
- [ ] (Optional) Add more languages or UI themes

---

## 6. How to Resume
- To continue, just run:
  ```
  read docs/FRONTEND_SNAPSHOT.md
  ```
- I will pick up exactly where we left off!

---

**Snapshot saved: March 28, 2026**

You can now safely stop and resume later from this exact point!
