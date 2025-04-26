import React, { createContext, useContext, useState, useEffect, useRef } from 'react';
import Peer from 'simple-peer';
import { socketService } from '../services/websocket';

const WebRTCContext = createContext();

export const useWebRTC = () => useContext(WebRTCContext);

export const WebRTCProvider = ({ children }) => {
  const [localStream, setLocalStream] = useState(null);
  const [remoteStream, setRemoteStream] = useState(null);
  const [callStatus, setCallStatus] = useState('idle'); // idle, calling, ringing, connected
  const [currentContact, setCurrentContact] = useState(null);
  const [incomingCall, setIncomingCall] = useState(null);
  
  const peerRef = useRef(null);
  const localVideoRef = useRef(null);
  const remoteVideoRef = useRef(null);

  useEffect(() => {
    // Initialize websocket listeners
    socketService.on('call-offer', handleIncomingCall);
    socketService.on('call-answer', handleCallAccepted);
    socketService.on('ice-candidate', handleNewICECandidate);
    socketService.on('call-ended', handleCallEnded);

    return () => {
      // Cleanup
      socketService.off('call-offer');
      socketService.off('call-answer');
      socketService.off('ice-candidate');
      socketService.off('call-ended');
      if (localStream) {
        localStream.getTracks().forEach(track => track.stop());
      }
    };
  }, []);

  const setupLocalStream = async () => {
    try {
      const stream = await navigator.mediaDevices.getUserMedia({ 
        video: true, 
        audio: true 
      });
      setLocalStream(stream);
      
      if (localVideoRef.current) {
        localVideoRef.current.srcObject = stream;
      }
      
      return stream;
    } catch (error) {
      console.error("Error accessing media devices:", error);
      throw error;
    }
  };

  const initiateCall = async (contactId) => {
    try {
      const stream = localStream || await setupLocalStream();
      setCallStatus('calling');
      setCurrentContact(contactId);
      
      const peer = new Peer({
        initiator: true,
        trickle: true,
        stream: stream
      });

      peer.on('signal', data => {
        socketService.emit('call-user', {
          targetUserId: contactId,
          signalData: data
        });
      });

      peer.on('stream', stream => {
        setRemoteStream(stream);
        if (remoteVideoRef.current) {
          remoteVideoRef.current.srcObject = stream;
        }
      });

      peer.on('close', () => {
        cleanupPeerConnection();
      });

      peer.on('error', err => {
        console.error('Peer connection error:', err);
        cleanupPeerConnection();
      });

      peerRef.current = peer;
    } catch (error) {
      console.error("Error initiating call:", error);
      setCallStatus('idle');
    }
  };

  const handleIncomingCall = async ({ from, signal }) => {
    try {
      setIncomingCall({ from, signal });
      setCallStatus('ringing');
    } catch (error) {
      console.error("Error handling incoming call:", error);
    }
  };

  const acceptCall = async () => {
    try {
      if (!incomingCall) return;
      
      const stream = localStream || await setupLocalStream();
      setCurrentContact(incomingCall.from);
      
      const peer = new Peer({
        initiator: false,
        trickle: true,
        stream: stream
      });

      peer.on('signal', data => {
        socketService.emit('answer-call', {
          targetUserId: incomingCall.from,
          signalData: data
        });
      });

      peer.on('stream', stream => {
        setRemoteStream(stream);
        if (remoteVideoRef.current) {
          remoteVideoRef.current.srcObject = stream;
        }
      });

      peer.signal(incomingCall.signal);

      peer.on('close', () => {
        cleanupPeerConnection();
      });

      peer.on('error', err => {
        console.error('Peer connection error:', err);
        cleanupPeerConnection();
      });

      peerRef.current = peer;
      setCallStatus('connected');
      setIncomingCall(null);
    } catch (error) {
      console.error("Error accepting call:", error);
      setIncomingCall(null);
      setCallStatus('idle');
    }
  };

  const rejectCall = () => {
    if (!incomingCall) return;
    
    socketService.emit('reject-call', {
      targetUserId: incomingCall.from
    });
    
    setIncomingCall(null);
    setCallStatus('idle');
  };

  const handleCallAccepted = ({ signal }) => {
    peerRef.current.signal(signal);
    setCallStatus('connected');
  };

  const handleNewICECandidate = ({ candidate }) => {
    if (peerRef.current) {
      peerRef.current.signal({ candidate });
    }
  };

  const endCall = () => {
    if (currentContact) {
      socketService.emit('end-call', {
        targetUserId: currentContact
      });
    }

    cleanupPeerConnection();
  };

  const handleCallEnded = () => {
    cleanupPeerConnection();
  };

  const cleanupPeerConnection = () => {
    if (peerRef.current) {
      peerRef.current.destroy();
      peerRef.current = null;
    }

    if (remoteStream) {
      remoteStream.getTracks().forEach(track => track.stop());
      setRemoteStream(null);
    }

    setCallStatus('idle');
    setCurrentContact(null);
  };

  // Toggle audio/video
  const toggleAudio = () => {
    if (localStream) {
      const audioTrack = localStream.getAudioTracks()[0];
      if (audioTrack) {
        audioTrack.enabled = !audioTrack.enabled;
        return audioTrack.enabled;
      }
    }
    return false;
  };

  const toggleVideo = () => {
    if (localStream) {
      const videoTrack = localStream.getVideoTracks()[0];
      if (videoTrack) {
        videoTrack.enabled = !videoTrack.enabled;
        return videoTrack.enabled;
      }
    }
    return false;
  };

  return (
    <WebRTCContext.Provider value={{
      localStream,
      remoteStream,
      callStatus,
      currentContact,
      incomingCall,
      localVideoRef,
      remoteVideoRef,
      initiateCall,
      acceptCall,
      rejectCall,
      endCall,
      toggleAudio,
      toggleVideo
    }}>
      {children}
    </WebRTCContext.Provider>
  );
};