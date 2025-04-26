package com.videochat.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("user_status")
public class UserStatus {

    @Id
    private String id;

    @Indexed
    private Long userId;

    private boolean online;

    private LocalDateTime lastSeen;

    private String sessionId;
}