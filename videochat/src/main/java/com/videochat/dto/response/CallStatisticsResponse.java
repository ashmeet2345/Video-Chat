package com.videochat.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CallStatisticsResponse {
    private int totalCalls;
    private int outgoingCalls;
    private int incomingCalls;
    private int answeredCalls;
    private int missedCalls;
    private int rejectedCalls;
    private long totalDurationSeconds;
    private Long mostCalledContactId;
    private Map<Long, Integer> callsByContact;
}