import React from 'react';
//import './CallNotification.css';

const CallNotification = ({ from, onAccept, onReject }) => {
  return (
    <div className="call-notification">
      <div className="notification-content">
        <h3>Incoming Call</h3>
        <p>Call from: {from}</p>
        <div className="notification-actions">
          <button 
            className="accept-button"
            onClick={onAccept}
          >
            Accept
          </button>
          <button 
            className="reject-button"
            onClick={onReject}
          >
            Reject
          </button>
        </div>
      </div>
    </div>
  );
};

export default CallNotification;