package com.videochat.config;

import com.videochat.service.UserService;
import com.videochat.service.UserStatusService;
import com.videochat.service.WebSocketService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final WebSocketService webSocketService;
    private final UserStatusService userStatusService;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        Object authentication = headerAccessor.getUser();

        if (authentication instanceof UsernamePasswordAuthenticationToken) {
            UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) authentication;
            if (auth.getPrincipal() instanceof com.videochat.model.User) {
                com.videochat.model.User user = (com.videochat.model.User) auth.getPrincipal();
                String sessionId = headerAccessor.getSessionId();

                // Update user status
                userStatusService.updateUserStatus(user, true, sessionId);

                // Notify others
                webSocketService.notifyUserStatus(user.getId(), true);
            }
        }
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        Object authentication = headerAccessor.getUser();

        if (authentication instanceof UsernamePasswordAuthenticationToken) {
            UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) authentication;
            if (auth.getPrincipal() instanceof com.videochat.model.User) {
                com.videochat.model.User user = (com.videochat.model.User) auth.getPrincipal();

                // Update user status
                userStatusService.updateUserStatus(user, false, null);

                // Notify others
                webSocketService.notifyUserStatus(user.getId(), false);
            }
        }
    }
}