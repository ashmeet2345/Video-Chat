package com.videochat.service;

import com.videochat.dto.response.CallStatisticsResponse;
import com.videochat.model.CallHistory;
import com.videochat.model.CallStatus;
import com.videochat.model.User;
import com.videochat.repository.CallHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CallHistoryService {

    private final CallHistoryRepository callHistoryRepository;

    public List<CallHistory> getCallHistoryForUser(User user) {
        return callHistoryRepository.findByCallerOrReceiverOrderByStartTimeDesc(user, user);
    }

    public CallHistory createCallRecord(User caller, User receiver) {
        CallHistory callHistory = CallHistory.builder()
                .caller(caller)
                .receiver(receiver)
                .status(CallStatus.MISSED) // Default status, will be updated when call is answered/rejected
                .build();

        return callHistoryRepository.save(callHistory);
    }

    public CallHistory updateCallStatus(Long callId, CallStatus status) {
        CallHistory callHistory = callHistoryRepository.findById(callId)
                .orElseThrow(() -> new IllegalArgumentException("Call not found"));

        callHistory.setStatus(status);
        return callHistoryRepository.save(callHistory);
    }

    public CallHistory endCall(Long callId) {
        CallHistory callHistory = callHistoryRepository.findById(callId)
                .orElseThrow(() -> new IllegalArgumentException("Call not found"));

        LocalDateTime endTime = LocalDateTime.now();
        callHistory.setEndTime(endTime);

        // Calculate duration in seconds
        long durationSeconds = ChronoUnit.SECONDS.between(callHistory.getStartTime(), endTime);
        callHistory.setDurationSeconds((int) durationSeconds);

        return callHistoryRepository.save(callHistory);
    }

    public CallStatisticsResponse getCallStatistics(User user, LocalDateTime from, LocalDateTime to) {
        if (from == null) {
            from = LocalDateTime.now().minusMonths(1);
        }

        if (to == null) {
            to = LocalDateTime.now();
        }

        List<CallHistory> calls = callHistoryRepository.findByCallerOrReceiverAndStartTimeBetween(
                user, user, from, to);

        int totalCalls = calls.size();
        int outgoingCalls = (int) calls.stream().filter(call -> call.getCaller().getId().equals(user.getId())).count();
        int incomingCalls = totalCalls - outgoingCalls;

        int answeredCalls = (int) calls.stream().filter(call -> call.getStatus() == CallStatus.ANSWERED).count();
        int missedCalls = (int) calls.stream().filter(call -> call.getStatus() == CallStatus.MISSED).count();
        int rejectedCalls = (int) calls.stream().filter(call -> call.getStatus() == CallStatus.REJECTED).count();

        long totalDuration = calls.stream()
                .filter(call -> call.getDurationSeconds() != null)
                .mapToLong(CallHistory::getDurationSeconds)
                .sum();

        // Group by contact
        Map<Long, Integer> callsByContact = new HashMap<>();
        for (CallHistory call : calls) {
            Long contactId;
            if (call.getCaller().getId().equals(user.getId())) {
                contactId = call.getReceiver().getId();
            } else {
                contactId = call.getCaller().getId();
            }

            callsByContact.put(contactId, callsByContact.getOrDefault(contactId, 0) + 1);
        }

        // Find most called contact
        Long mostCalledContactId = callsByContact.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

        return CallStatisticsResponse.builder()
                .totalCalls(totalCalls)
                .outgoingCalls(outgoingCalls)
                .incomingCalls(incomingCalls)
                .answeredCalls(answeredCalls)
                .missedCalls(missedCalls)
                .rejectedCalls(rejectedCalls)
                .totalDurationSeconds(totalDuration)
                .mostCalledContactId(mostCalledContactId)
                .callsByContact(callsByContact)
                .build();
    }
}