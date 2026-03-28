import './App.css';
import { useState, useEffect } from 'react';
import ChatWindow from './components/ChatWindow';

function App() {
  const [sessionId, setSessionId] = useState(null);
  const [language, setLanguage] = useState('en');
  const [loading, setLoading] = useState(true);

  // Create a new conversation session on component mount
  useEffect(() => {
    createNewSession(language);
  }, []);

  const createNewSession = async (lang) => {
    try {
      setLoading(true);
      const response = await fetch(`http://localhost:8080/api/chat/new?language=${lang}`);
      if (response.ok) {
        const data = await response.json();
        setSessionId(data.sessionId);
      } else {
        console.error('Failed to create session');
      }
    } catch (error) {
      console.error('Error creating session:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleLanguageChange = (newLanguage) => {
    setLanguage(newLanguage);
    // Create new session with new language
    createNewSession(newLanguage);
  };

  if (loading) {
    return (
      <div className="App loading">
        <div className="spinner">
          <div className="spinner-circle"></div>
          <p>Loading Satu AI...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="App">
      {sessionId ? (
        <ChatWindow
          sessionId={sessionId}
          language={language}
          onLanguageChange={handleLanguageChange}
        />
      ) : (
        <div className="error-container">
          <p>❌ Failed to initialize chat. Please refresh the page.</p>
        </div>
      )}
    </div>
  );
}

export default App;
