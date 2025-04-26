import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import './Header.css';

const Header = ({ user, onLogout }) => {
  const navigate = useNavigate();
  
  const handleLogout = () => {
    onLogout();
    navigate('/login');
  };
  
  return (
    <header className="app-header">
      <div className="header-container">
        <Link to="/" className="logo">
          Video Chat
        </Link>
        
        <nav className="nav-menu">
          {user ? (
            <>
              <span className="welcome-text">Hello, {user.username}</span>
              <button onClick={handleLogout} className="logout-button">
                Logout
              </button>
            </>
          ) : (
            <>
              <Link to="/login" className="nav-link">Login</Link>
              <Link to="/register" className="nav-link register">Register</Link>
            </>
          )}
        </nav>
      </div>
    </header>
  );
};

export default Header;