import React, { useState } from 'react';
import './ContactSearch.css';

const ContactSearch = ({ onSearch }) => {
  const [searchTerm, setSearchTerm] = useState('');

  const handleChange = (e) => {
    const value = e.target.value;
    setSearchTerm(value);
    onSearch(value);
  };

  const handleClear = () => {
    setSearchTerm('');
    onSearch('');
  };

  return (
    <div className="contact-search">
      <input
        type="text"
        placeholder="Search contacts..."
        value={searchTerm}
        onChange={handleChange}
        className="search-input"
      />
      {searchTerm && (
        <button
          className="search-clear"
          onClick={handleClear}
        >
          ✕
        </button>
      )}
    </div>
  );
};

export default ContactSearch;