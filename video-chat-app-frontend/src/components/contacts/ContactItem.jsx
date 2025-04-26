import React from 'react';
import './ContactItem.css';

const ContactItem = ({ 
  contact, 
  onSelectContact, 
  onAcceptContact, 
  onRejectContact, 
  onRemoveContact 
}) => {
  const isPending = contact.status === 'pending';
  const isIncoming = isPending && contact.direction === 'incoming';
  
  const handleStartCall = () => {
    onSelectContact(contact.id);
  };

  return (
    <div className="contact-item">
      <div className="contact-info">
        <div className="contact-avatar">
          {contact.username.charAt(0).toUpperCase()}
        </div>
        <div className="contact-details">
          <div className="contact-name">{contact.username}</div>
          {contact.email && <div className="contact-email">{contact.email}</div>}
          {isPending && (
            <div className="contact-status">
              {isIncoming ? 'Wants to connect with you' : 'Request sent'}
            </div>
          )}
        </div>
      </div>

      <div className="contact-actions">
        {isPending ? (
          isIncoming ? (
            <>
              <button 
                className="contact-action accept" 
                onClick={() => onAcceptContact(contact.id)}
              >
                Accept
              </button>
              <button 
                className="contact-action reject" 
                onClick={() => onRejectContact(contact.id)}
              >
                Reject
              </button>
            </>
          ) : (
            <button 
              className="contact-action cancel" 
              onClick={() => onRemoveContact(contact.id)}
            >
              Cancel
            </button>
          )
        ) : (
          <>
            <button 
              className="contact-action call" 
              onClick={handleStartCall}
            >
              Call
            </button>
            <button 
              className="contact-action remove" 
              onClick={() => onRemoveContact(contact.id)}
            >
              Remove
            </button>
          </>
        )}
      </div>
    </div>
  );
};

export default ContactItem;