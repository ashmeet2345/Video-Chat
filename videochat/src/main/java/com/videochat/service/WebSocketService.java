package com.videochat.service;

import com.videochat.dto.response.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class WebSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    public void notifyContactRequest(Long userId, UserResponse requester) {
        messagingTemplate.convertAndSendToUser(
                userId.toString(),
                "/queue/contacts",
                requester
        );
    }

    public void notifyContactUpdate(Long userId, String message) {
        messagingTemplate.convertAndSendToUser(
                userId.toString(),
                "/queue/contacts/updates",
                message
        );
    }

    public void notifyIncomingCall(Long userId, Long callId, UserResponse caller) {
        messagingTemplate.convertAndSendToUser(
                userId.toString(),
                "/queue/calls/incoming",
                Map.of(
                        "callId", callId,
                        "caller", caller
                )
        );
    }

    public void notifyCallAccepted(Long userId, Long callId) {
        messagingTemplate.convertAndSendToUser(
                userId.toString(),
                "/queue/calls/accepted",
                Map.of("callId", callId)
        );
    }

    public void notifyCallRejected(Long userId, Long callId) {
        messagingTemplate.convertAndSendToUser(
                userId.toString(),
                "/queue/calls/rejected",
                Map.of("callId", callId)
        );
    }

    public void notifyCallEnded(Long userId, Long callId) {
        messagingTemplate.convertAndSendToUser(
                userId.toString(),
                "/queue/calls/ended",
                Map.of("callId", callId)
        );
    }

    public void sendToUser(Long userId, String destination, Object payload) {
        messagingTemplate.convertAndSendToUser(
                userId.toString(),
                destination,
                payload
        );
    }

    public void broadcastToTopic(String topic, Object payload) {
        messagingTemplate.convertAndSend(
                "/topic/" + topic,
                payload
        );
    }

    public void notifyUserStatus(Long userId, boolean online) {
        broadcastToTopic("user-status", Map.of(
                "userId", userId,
                "online", online
        ));
    }
}