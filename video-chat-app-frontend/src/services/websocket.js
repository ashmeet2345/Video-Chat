import { io } from 'socket.io-client';

const API_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080';

class WebSocketService {
  constructor() {
    this.socket = null;
    this.connected = false;
  }

  connect(token) {
    if (this.socket && this.connected) return;

    this.socket = io(API_URL, {
      auth: {
        token
      },
      transports: ['websocket'],
      reconnection: true,
      reconnectionAttempts: 5,
      reconnectionDelay: 1000
    });

    this.socket.on('connect', () => {
      console.log('WebSocket connected');
      this.connected = true;
    });

    this.socket.on('disconnect', () => {
      console.log('WebSocket disconnected');
      this.connected = false;
    });

    this.socket.on('error', (error) => {
      console.error('WebSocket error:', error);
    });

    return this.socket;
  }

  disconnect() {
    if (this.socket) {
      this.socket.disconnect();
      this.socket = null;
      this.connected = false;
    }
  }

  emit(event, data) {
    if (!this.socket || !this.connected) {
      console.error('Socket not connected. Cannot emit event:', event);
      return;
    }
    this.socket.emit(event, data);
  }

  on(event, callback) {
    if (!this.socket) {
      console.error('Socket not initialized. Cannot listen to event:', event);
      return;
    }
    this.socket.on(event, callback);
  }

  off(event, callback) {
    if (!this.socket) return;
    if (callback) {
      this.socket.off(event, callback);
    } else {
      this.socket.off(event);
    }
  }
}

export const socketService = new WebSocketService();