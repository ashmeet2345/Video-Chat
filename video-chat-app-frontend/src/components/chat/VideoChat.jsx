import React, { useEffect, useState } from 'react';
import { useWebRTC } from '../../context/WebRTCContext';
import Controls from './Controls';
import CallNotification from './CallNotification';
import './VideoChat.css';

const VideoChat = ({ contactId }) => {
  const { 
    localVideoRef, 
    remoteVideoRef, 
    initiateCall, 
    callStatus, 
    currentContact,
    incomingCall,
    acceptCall,
    rejectCall,
    endCall
  } = useWebRTC();
  
  const [isAudioEnabled, setIsAudioEnabled] = useState(true);
  const [isVideoEnabled, setIsVideoEnabled] = useState(true);

  useEffect(() => {
    // If contactId is provided, initiate a call automatically
    if (contactId && callStatus === 'idle') {
      handleStartCall();
    }
  }, [contactId]);

  const handleStartCall = async () => {
    try {
      await initiateCall(contactId);
    } catch (error) {
      console.error("Failed to start call:", error);
    }
  };

  const handleEndCall = () => {
    endCall();
  };

  const handleAcceptCall = () => {
    acceptCall();
  };

  const handleRejectCall = () => {
    rejectCall();
  };

  const toggleAudio = () => {
    const isEnabled = useWebRTC().toggleAudio();
    setIsAudioEnabled(isEnabled);
  };

  const toggleVideo = () => {
    const isEnabled = useWebRTC().toggleVideo();
    setIsVideoEnabled(isEnabled);
  };

  return (
    <div className="video-chat-container">
      {/* Incoming call notification */}
      {incomingCall && callStatus === 'ringing' && (
        <CallNotification
          from={incomingCall.from}
          onAccept={handleAcceptCall}
          onReject={handleRejectCall}
        />
      )}

      {/* Video containers */}
      <div className="video-grid">
        <div className={`remote-video-container ${callStatus === 'connected' ? 'active' : ''}`}>
          <video 
            ref={remoteVideoRef} 
            autoPlay 
            playsInline 
            className="remote-video"
          />
          {callStatus !== 'connected' && (
            <div className="video-placeholder">
              {callStatus === 'calling' ? 'Calling...' : 'No active call'}
            </div>
          )}
        </div>
        
        <div className="local-video-container">
          <video 
            ref={localVideoRef} 
            autoPlay 
            playsInline 
            muted // Mute local video to prevent feedback
            className="local-video"
          />
        </div>
      </div>

      {/* Call controls */}
      <Controls
        isAudioEnabled={isAudioEnabled}
        isVideoEnabled={isVideoEnabled}
        callStatus={callStatus}
        onStartCall={handleStartCall}
        onEndCall={handleEndCall}
        onToggleAudio={toggleAudio}
        onToggleVideo={toggleVideo}
      />
    </div>
  );
};

export default VideoChat;