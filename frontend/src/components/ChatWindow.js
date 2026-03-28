import React, { useState, useEffect, useRef } from 'react';
import './ChatWindow.css';

/**
 * ChatWindow Component - Main chat interface
 * Displays messages and message input
 */
function ChatWindow({ sessionId, language, onLanguageChange }) {
  const [messages, setMessages] = useState([]);
  const [input, setInput] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const messagesEndRef = useRef(null);

  // Scroll to bottom whenever messages change
  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  // Fetch conversation history on component mount
  useEffect(() => {
    if (sessionId) {
      fetchConversationHistory();
    }
  }, [sessionId]);

  const fetchConversationHistory = async () => {
    try {
      const response = await fetch(`http://localhost:8080/api/chat/history/${sessionId}`);
      if (response.ok) {
        const data = await response.json();
        setMessages(data.messages || []);
        setError(null);
      }
    } catch (err) {
      console.error('Error fetching conversation history:', err);
      setError('Failed to load conversation history');
    }
  };

  const handleSendMessage = async (e) => {
    e.preventDefault();

    if (!input.trim()) {
      return;
    }

    if (!sessionId) {
      setError('No active session. Please start a new chat.');
      return;
    }

    const userMessage = {
      sender: 'user',
      content: input,
      language: language,
      timestamp: new Date().toLocaleString(),
    };

    // Add user message to UI immediately
    setMessages([...messages, userMessage]);
    setInput('');
    setLoading(true);
    setError(null);

    try {
      // Send message to backend
      const response = await fetch('http://localhost:8080/api/chat/send', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          sessionId: sessionId,
          message: input,
          language: language,
        }),
      });

      if (response.ok) {
        const assistantMessage = await response.json();
        setMessages(prev => [...prev, assistantMessage]);
      } else {
        const error = await response.json();
        setError(error.error || 'Failed to send message');
        // Remove the user message if there was an error
        setMessages(prev => prev.slice(0, -1));
      }
    } catch (err) {
      console.error('Error sending message:', err);
      setError('Failed to send message. Please try again.');
      // Remove the user message if there was an error
      setMessages(prev => prev.slice(0, -1));
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="chat-window">
      <div className="chat-header">
        <h1>Satu AI</h1>
        <div className="language-selector">
          <select value={language} onChange={(e) => onLanguageChange(e.target.value)}>
            <option value="en">English</option>
            <option value="hi">हिंदी</option>
            <option value="mr">मराठी</option>
          </select>
        </div>
      </div>

      <div className="messages-container">
        {messages.length === 0 && !error && (
          <div className="welcome-message">
            <p>👋 Hi! I'm Satu, your AI best friend!</p>
            <p>What would you like to chat about today?</p>
          </div>
        )}

        {messages.map((msg, index) => (
          <div key={index} className={`message ${msg.sender}`}>
            <div className="message-content">
              <span className="sender-badge">{msg.sender === 'user' ? 'You' : 'Satu'}</span>
              <p>{msg.content}</p>
              <small className="message-time">{msg.timestamp}</small>
            </div>
          </div>
        ))}

        {error && (
          <div className="error-message">
            <p>⚠️ {error}</p>
          </div>
        )}

        {loading && (
          <div className="message assistant">
            <div className="message-content">
              <span className="sender-badge">Satu</span>
              <div className="typing-indicator">
                <span></span>
                <span></span>
                <span></span>
              </div>
            </div>
          </div>
        )}

        <div ref={messagesEndRef} />
      </div>

      <form onSubmit={handleSendMessage} className="message-input-form">
        <input
          type="text"
          value={input}
          onChange={(e) => setInput(e.target.value)}
          placeholder="Type your message here..."
          disabled={loading}
          className="message-input"
        />
        <button type="submit" disabled={loading || !input.trim()} className="send-button">
          {loading ? '⏳' : '📤 Send'}
        </button>
      </form>
    </div>
  );
}

export default ChatWindow;

