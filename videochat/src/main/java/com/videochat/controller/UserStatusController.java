package com.videochat.controller;

import com.videochat.model.User;
import com.videochat.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/status")
@RequiredArgsConstructor
public class UserStatusController {

    private final UserStatusService userStatusService;

    @GetMapping("/online")
    public ResponseEntity<List<Long>> getOnlineUsers() {
        return ResponseEntity.ok(userStatusService.getOnlineUsers());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getUserStatus(@PathVariable Long userId) {
        boolean online = userStatusService.isUserOnline(userId);
        LocalDateTime lastSeen = userStatusService.getLastSeen(userId);

        Map<String, Object> response = new HashMap<>();
        response.put("userId", userId);
        response.put("online", online);
        response.put("lastSeen", lastSeen);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/heartbeat")
    public ResponseEntity<Void> sendHeartbeat(@AuthenticationPrincipal User currentUser) {
        userStatusService.updateUserStatus(currentUser, true, null);
        return ResponseEntity.ok().build();
    }
}