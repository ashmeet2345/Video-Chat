import React from 'react';
//import './Controls.css';

const Controls = ({
  isAudioEnabled,
  isVideoEnabled,
  callStatus,
  onStartCall,
  onEndCall,
  onToggleAudio,
  onToggleVideo
}) => {
  const isCallActive = callStatus === 'connected' || callStatus === 'calling';

  return (
    <div className="controls-container">
      <button 
        className={`control-button ${!isAudioEnabled ? 'disabled' : ''}`}
        onClick={onToggleAudio}
        disabled={callStatus === 'idle'}
        title={isAudioEnabled ? "Mute Audio" : "Unmute Audio"}
      >
        {isAudioEnabled ? "Mic On" : "Mic Off"}
      </button>

      {!isCallActive ? (
        <button 
          className="control-button start-call" 
          onClick={onStartCall}
        >
          Start Call
        </button>
      ) : (
        <button 
          className="control-button end-call" 
          onClick={onEndCall}
        >
          End Call
        </button>
      )}

      <button 
        className={`control-button ${!isVideoEnabled ? 'disabled' : ''}`}
        onClick={onToggleVideo}
        disabled={callStatus === 'idle'}
        title={isVideoEnabled ? "Turn Off Camera" : "Turn On Camera"}
      >
        {isVideoEnabled ? "Camera On" : "Camera Off"}
      </button>
    </div>
  );
};

export default Controls;