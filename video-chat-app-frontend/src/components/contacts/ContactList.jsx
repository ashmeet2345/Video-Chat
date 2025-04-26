import React, { useState, useEffect } from 'react';
import { contactsApi } from '../../services/api';
import ContactItem from './ContactItem';
import ContactSearch from './ContactSearch';
import AddContact from './AddContact';
import './ContactList.css';

const ContactList = ({ onSelectContact }) => {
  const [contacts, setContacts] = useState([]);
  const [filteredContacts, setFilteredContacts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [showAddContact, setShowAddContact] = useState(false);

  useEffect(() => {
    fetchContacts();
  }, []);

  const fetchContacts = async () => {
    try {
      setLoading(true);
      const response = await contactsApi.getContacts();
      setContacts(response.data);
      setFilteredContacts(response.data);
      setLoading(false);
    } catch (error) {
      console.error('Error fetching contacts:', error);
      setError('Failed to load contacts. Please try again later.');
      setLoading(false);
    }
  };

  const handleSearch = (searchTerm) => {
    if (!searchTerm.trim()) {
      setFilteredContacts(contacts);
      return;
    }

    const filtered = contacts.filter(contact => 
      contact.username.toLowerCase().includes(searchTerm.toLowerCase()) ||
      (contact.email && contact.email.toLowerCase().includes(searchTerm.toLowerCase()))
    );
    
    setFilteredContacts(filtered);
  };

  const handleAddContact = async (userId) => {
    try {
      await contactsApi.addContact(userId);
      fetchContacts(); // Refresh contacts
      setShowAddContact(false);
    } catch (error) {
      console.error('Error adding contact:', error);
      alert('Failed to add contact. Please try again.');
    }
  };

  const handleAcceptContact = async (contactId) => {
    try {
      await contactsApi.acceptContact(contactId);
      fetchContacts(); // Refresh contacts
    } catch (error) {
      console.error('Error accepting contact:', error);
      alert('Failed to accept contact request. Please try again.');
    }
  };

  const handleRejectContact = async (contactId) => {
    try {
      await contactsApi.rejectContact(contactId);
      fetchContacts(); // Refresh contacts
    } catch (error) {
      console.error('Error rejecting contact:', error);
      alert('Failed to reject contact request. Please try again.');
    }
  };
  
  const handleRemoveContact = async (contactId) => {
    if (window.confirm('Are you sure you want to remove this contact?')) {
      try {
        await contactsApi.removeContact(contactId);
        fetchContacts(); // Refresh contacts
      } catch (error) {
        console.error('Error removing contact:', error);
        alert('Failed to remove contact. Please try again.');
      }
    }
  };

  const toggleAddContact = () => {
    setShowAddContact(!showAddContact);
  };

  return (
    <div className="contact-list-container">
      <div className="contact-list-header">
        <h2>Contacts</h2>
        <button 
          className="add-contact-button" 
          onClick={toggleAddContact}
        >
          {showAddContact ? 'Cancel' : 'Add Contact'}
        </button>
      </div>

      {showAddContact ? (
        <AddContact onAddContact={handleAddContact} onCancel={toggleAddContact} />
      ) : (
        <ContactSearch onSearch={handleSearch} />
      )}

      <div className="contact-list">
        {loading ? (
          <div className="contact-list-loading">Loading contacts...</div>
        ) : error ? (
          <div className="contact-list-error">{error}</div>
        ) : filteredContacts.length === 0 ? (
          <div className="contact-list-empty">
            {contacts.length === 0 
              ? "You don't have any contacts yet. Add some to start chatting!" 
              : "No contacts match your search."}
          </div>
        ) : (
          <>
            {/* Pending contact requests */}
            {filteredContacts.some(contact => contact.status === 'pending') && (
              <div className="contact-section">
                <h3>Pending Requests</h3>
                {filteredContacts
                  .filter(contact => contact.status === 'pending')
                  .map(contact => (
                    <ContactItem
                      key={contact.id}
                      contact={contact}
                      onSelectContact={onSelectContact}
                      onAcceptContact={handleAcceptContact}
                      onRejectContact={handleRejectContact}
                      onRemoveContact={handleRemoveContact}
                    />
                  ))}
              </div>
            )}
            
            {/* Active contacts */}
            <div className="contact-section">
              <h3>Contacts</h3>
              {filteredContacts
                .filter(contact => contact.status === 'accepted')
                .map(contact => (
                  <ContactItem
                    key={contact.id}
                    contact={contact}
                    onSelectContact={onSelectContact}
                    onRemoveContact={handleRemoveContact}
                  />
                ))}
            </div>
          </>
        )}
      </div>
    </div>
  );
};

export default ContactList;