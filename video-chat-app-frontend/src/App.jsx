import React, { useEffect, useState } from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { WebRTCProvider } from './context/WebRTCContext';
import Login from './components/auth/Login';
import Register from './components/auth/Register';
import VideoChat from './components/chat/VideoChat';
import ContactList from './components/contacts/ContactList';
import Header from './components/common/Header';
import { authApi } from './services/api';
import { socketService } from './services/websocket';
import './App.css';

const App = () => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [selectedContact, setSelectedContact] = useState(null);

  useEffect(() => {
    // Check if user is logged in
    const checkAuth = async () => {
      const token = localStorage.getItem('token');
      
      if (token) {
        try {
          const response = await authApi.getCurrentUser();
          setUser(response.data);
          // Connect to WebSocket
          socketService.connect(token);
        } catch (error) {
          localStorage.removeItem('token');
        }
      }
      
      setLoading(false);
    };
    
    checkAuth();
    
    return () => {
      // Cleanup WebSocket connection on unmount
      socketService.disconnect();
    };
  }, []);

  const handleLogin = (userData, token) => {
    setUser(userData);
    localStorage.setItem('token', token);
    socketService.connect(token);
  };

  const handleLogout = () => {
    setUser(null);
    localStorage.removeItem('token');
    socketService.disconnect();
  };

  const handleContactSelect = (contactId) => {
    setSelectedContact(contactId);
  };

  if (loading) {
    return <div className="loading">Loading...</div>;
  }

  return (
    <Router>
      <WebRTCProvider>
        <div className="app">
          <Header user={user} onLogout={handleLogout} />
          
          <Routes>
            <Route 
              path="/login" 
              element={
                user ? <Navigate to="/chat" /> : <Login onLogin={handleLogin} />
              } 
            />
            <Route 
              path="/register" 
              element={
                user ? <Navigate to="/chat" /> : <Register />
              } 
            />
            <Route 
              path="/chat" 
              element={
                user ? (
                  <div className="chat-container">
                    <ContactList onSelectContact={handleContactSelect} />
                    <VideoChat contactId={selectedContact} />
                  </div>
                ) : (
                  <Navigate to="/login" />
                )
              } 
            />
            <Route 
              path="/" 
              element={<Navigate to={user ? "/chat" : "/login"} />} 
            />
          </Routes>
        </div>
      </WebRTCProvider>
    </Router>
  );
};

export default App;