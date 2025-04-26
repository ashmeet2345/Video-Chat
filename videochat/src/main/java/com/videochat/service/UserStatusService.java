package com.videochat.service;

import com.videochat.model.User;
import com.videochat.model.UserStatus;
import com.videochat.repository.UserStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserStatusService {

    private final UserStatusRepository userStatusRepository;

    public UserStatus updateUserStatus(User user, boolean online, String sessionId) {
        UserStatus status = userStatusRepository.findByUserId(user.getId())
                .orElse(UserStatus.builder()
                        .id(UUID.randomUUID().toString())
                        .userId(user.getId())
                        .build());

        status.setOnline(online);
        status.setLastSeen(LocalDateTime.now());

        if (sessionId != null) {
            status.setSessionId(sessionId);
        }

        return userStatusRepository.save(status);
    }

    public boolean isUserOnline(Long userId) {
        return userStatusRepository.findByUserId(userId)
                .map(UserStatus::isOnline)
                .orElse(false);
    }

    public List<Long> getOnlineUsers() {
        List<Long> onlineUserIds = new ArrayList<>();
        userStatusRepository.findByOnline(true).forEach(status -> onlineUserIds.add(status.getUserId()));
        return onlineUserIds;
    }

    public LocalDateTime getLastSeen(Long userId) {
        return userStatusRepository.findByUserId(userId)
                .map(UserStatus::getLastSeen)
                .orElse(null);
    }
}