import React, { useState } from 'react';
import { userApi } from '../../services/api';
import './AddContact.css';

const AddContact = ({ onAddContact, onCancel }) => {
  const [searchTerm, setSearchTerm] = useState('');
  const [searchResults, setSearchResults] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleSearch = async () => {
    if (!searchTerm.trim()) return;
    
    try {
      setLoading(true);
      setError('');
      
      // Make an API call to search for users
      const response = await userApi.getUsers({ search: searchTerm });
      setSearchResults(response.data);
      
      if (response.data.length === 0) {
        setError('No users found matching your search.');
      }
      
      setLoading(false);
    } catch (error) {
      console.error('Error searching users:', error);
      setError('Failed to search for users. Please try again.');
      setLoading(false);
    }
  };

  const handleKeyDown = (e) => {
    if (e.key === 'Enter') {
      handleSearch();
    }
  };

  const handleAdd = (userId) => {
    onAddContact(userId);
  };

  return (
    <div className="add-contact">
      <div className="search-container">
        <input
          type="text"
          placeholder="Search by username or email..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          onKeyDown={handleKeyDown}
          className="add-contact-input"
        />
        <button 
          onClick={handleSearch} 
          disabled={loading || !searchTerm.trim()}
          className="search-button"
        >
          {loading ? 'Searching...' : 'Search'}
        </button>
      </div>
      
      {error && <div className="add-contact-error">{error}</div>}
      
      <div className="search-results">
        {searchResults.map(user => (
          <div key={user.id} className="user-item">
            <div className="user-info">
              <div className="user-avatar">
                {user.username.charAt(0).toUpperCase()}
              </div>
              <div className="user-details">
                <div className="user-name">{user.username}</div>
                {user.email && <div className="user-email">{user.email}</div>}
              </div>
            </div>
            <button 
              onClick={() => handleAdd(user.id)}
              className="add-button"
            >
              Add
            </button>
          </div>
        ))}
      </div>
    </div>
  );
};

export default AddContact;