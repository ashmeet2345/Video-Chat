package com.videochat.dto.response;

import com.videochat.model.ContactDirection;
import com.videochat.model.ContactStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ContactResponse {
    private Long id;
    private Long userId;
    private String username;
    private String email;
    private ContactStatus status;
    private ContactDirection direction;
}