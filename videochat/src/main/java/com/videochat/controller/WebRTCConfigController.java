package com.videochat.controller;

import com.videochat.config.WebRTCConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/webrtc")
@RequiredArgsConstructor
public class WebRTCConfigController {

    private final WebRTCConfig webRTCConfig;

    @GetMapping("/config")
    public ResponseEntity<Map<String, Object>> getWebRTCConfig() {
        return ResponseEntity.ok(webRTCConfig.getIceServers());
    }
}