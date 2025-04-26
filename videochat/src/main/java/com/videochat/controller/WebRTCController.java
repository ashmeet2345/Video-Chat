package com.videochat.controller;

import com.videochat.model.User;
import com.videochat.service.UserService;
import com.videochat.service.WebSocketService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class WebRTCController {

    private final WebSocketService webSocketService;
    private final UserService userService;

    @MessageMapping("/webrtc/offer")
    public void handleOffer(@Payload Map<String, Object> payload, @AuthenticationPrincipal User currentUser) {
        Long recipientId = Long.valueOf(payload.get("recipientId").toString());

        // Forward the offer to the recipient
        Map<String, Object> message = Map.of(
                "type", "offer",
                "offer", payload.get("offer"),
                "callId", payload.get("callId"),
                "senderId", currentUser.getId()
        );

        webSocketService.sendToUser(recipientId, "/queue/webrtc", message);
    }

    @MessageMapping("/webrtc/answer")
    public void handleAnswer(@Payload Map<String, Object> payload, @AuthenticationPrincipal User currentUser) {
        Long recipientId = Long.valueOf(payload.get("recipientId").toString());

        // Forward the answer to the recipient
        Map<String, Object> message = Map.of(
                "type", "answer",
                "answer", payload.get("answer"),
                "callId", payload.get("callId"),
                "senderId", currentUser.getId()
        );

        webSocketService.sendToUser(recipientId, "/queue/webrtc", message);
    }

    @MessageMapping("/webrtc/ice-candidate")
    public void handleIceCandidate(@Payload Map<String, Object> payload, @AuthenticationPrincipal User currentUser) {
        Long recipientId = Long.valueOf(payload.get("recipientId").toString());

        // Forward the ICE candidate to the recipient
        Map<String, Object> message = Map.of(
                "type", "ice-candidate",
                "candidate", payload.get("candidate"),
                "callId", payload.get("callId"),
                "senderId", currentUser.getId()
        );

        webSocketService.sendToUser(recipientId, "/queue/webrtc", message);
    }

    @MessageMapping("/webrtc/hangup")
    public void handleHangup(@Payload Map<String, Object> payload, @AuthenticationPrincipal User currentUser) {
        Long recipientId = Long.valueOf(payload.get("recipientId").toString());

        // Forward the hangup to the recipient
        Map<String, Object> message = Map.of(
                "type", "hangup",
                "callId", payload.get("callId"),
                "senderId", currentUser.getId()
        );

        webSocketService.sendToUser(recipientId, "/queue/webrtc", message);
    }
}