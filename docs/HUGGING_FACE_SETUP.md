# 🤗 Hugging Face API Setup - Free Forever

**Status**: Ready to configure  
**Cost**: Free (No credit card needed)  
**Time**: 5 minutes

---

## 🎯 Step-by-Step Setup

### Step 1: Get Hugging Face API Key (2 minutes)

1. **Go to**: https://huggingface.co/join
2. **Sign up** (completely free, no credit card required)
3. **Email verification** (check your inbox)
4. **Go to**: https://huggingface.co/settings/tokens
5. **Click**: "New token"
6. **Name it**: "Satu AI"
7. **Select**: "Read" permission
8. **Copy the token** (looks like: `hf_xxxxxxxxxxxxxxxxxxxxxxxx`)
   - ⚠️ Keep this secret! Don't share it!

---

### Step 2: Set Environment Variable (2 minutes)

**Windows PowerShell:**
```powershell
$env:HF_API_KEY = "hf_your_actual_token_here"
```

**Windows CMD:**
```cmd
set HF_API_KEY=hf_your_actual_token_here
```

**Linux/Mac:**
```bash
export HF_API_KEY="hf_your_actual_token_here"
```

**Verify it's set:**
```bash
echo $env:HF_API_KEY  # Windows PowerShell
echo $HF_API_KEY       # Linux/Mac
```

---

### Step 3: Restart Backend (1 minute)

```bash
cd backend
mvnw clean compile spring-boot:run
```

**Watch for:**
```
🤗 Hugging Face Mode Enabled - Using Hugging Face Inference API
Started SatuAiApplication in X.XXX seconds
```

When you see this, Hugging Face is connected! ✅

---

### Step 4: Refresh Browser & Test (30 seconds)

1. Go to: http://localhost:3000
2. Press: **F5** (Refresh)
3. Type: "Hello Satu"
4. Click: **Send**
5. Get real AI response! 🎉

---

## 💡 How It Works

```
User Message
    ↓
ChatService
    ↓
LLMConfig checks:
├─ HF_API_KEY exists? → Use Hugging Face ✅
├─ LLM_API_KEY exists? → Use OpenAI
└─ Neither? → Use Demo Mode

Hugging Face API
    ↓
Real AI Response
    ↓
User sees answer!
```

---

## 🔑 API Key Management

### Where to Find Your Key:
- https://huggingface.co/settings/tokens

### Keep It Safe:
- ⚠️ Don't commit to Git
- ⚠️ Don't share with anyone
- ⚠️ Don't post online
- ✅ Use environment variables

### If Leaked:
- Go to: https://huggingface.co/settings/tokens
- Delete the token
- Create a new one

---

## 🎭 Switch Back to Demo Mode (If Needed)

If you want to use demo mode again without API key:

**Option 1: Clear environment variable**
```bash
$env:HF_API_KEY = ""  # PowerShell
# or unset HF_API_KEY  # Linux/Mac
```

**Option 2: Restart without setting HF_API_KEY**
```bash
mvnw spring-boot:run
# Will use demo mode
```

---

## ❓ Troubleshooting

### Problem: "Not available" message still showing
**Solution:**
1. Check backend terminal for errors
2. Verify HF_API_KEY is set: `echo $env:HF_API_KEY`
3. Hard refresh browser: `Ctrl+F5`
4. Check Hugging Face API status: https://status.huggingface.co

### Problem: Response is slow
**Solution:**
- Hugging Face free tier has rate limits
- First call loads the model (~30 seconds)
- Subsequent calls are faster
- Use demo mode if you want instant responses

### Problem: Invalid API key error
**Solution:**
1. Double-check your token (copy from website again)
2. Make sure there are no extra spaces
3. Verify it starts with `hf_`
4. Restart backend after changing the key

### Problem: "Model not found"
**Solution:**
- Hugging Face might be loading the model
- This is normal on first use
- Wait a few seconds and try again
- Or use demo mode while waiting

---

## 📊 Comparison

| Feature | Demo Mode | Hugging Face | OpenAI |
|---------|-----------|--------------|--------|
| Cost | Free | Free | Paid |
| Real AI | ❌ | ✅ | ✅ |
| Speed | Instant | Slow first time | Fast |
| Quality | Generic | Good | Excellent |
| Setup | None | 5 min | 5 min |
| API Key | None | Free | Paid |

---

## 🚀 Quick Commands

```bash
# Set API key (PowerShell)
$env:HF_API_KEY = "hf_your_token"

# Restart backend
cd backend && mvnw spring-boot:run

# Test API key is set
echo $env:HF_API_KEY

# Switch back to demo mode
$env:HF_API_KEY = ""
```

---

## ✅ You're Ready!

1. ✅ Got Hugging Face account
2. ✅ Got API key
3. ✅ Set environment variable
4. ✅ Backend updated with HF support
5. ✅ Ready to use real AI!

---

## 🎉 Next Steps

1. **Set HF_API_KEY** environment variable
2. **Restart backend** with `mvnw spring-boot:run`
3. **Refresh browser** (F5)
4. **Chat with real AI!**

---

**Status**: Ready for real AI responses  
**Cost**: Completely free (Hugging Face free tier)  
**Quality**: Much better than demo mode!

Enjoy! 🤗

