package com.videochat.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@Getter
public class WebRTCConfig {

    @Value("${webrtc.stun.urls:stun:stun.l.google.com:19302}")
    private String stunUrls;

    @Value("${webrtc.turn.urls:}")
    private String turnUrls;

    @Value("${webrtc.turn.username:}")
    private String turnUsername;

    @Value("${webrtc.turn.credential:}")
    private String turnCredential;

    public Map<String, Object> getIceServers() {
        Map<String, Object> iceServers = new HashMap<>();

        // Add STUN server
        Map<String, String> stunServer = new HashMap<>();
        stunServer.put("urls", stunUrls);

        // Add TURN server if configured
        Map<String, Object> turnServer = new HashMap<>();
        if (!turnUrls.isEmpty()) {
            turnServer.put("urls", turnUrls);
            turnServer.put("username", turnUsername);
            turnServer.put("credential", turnCredential);

            iceServers.put("servers", new Object[]{stunServer, turnServer});
        } else {
            iceServers.put("servers", new Object[]{stunServer});
        }

        return iceServers;
    }
}