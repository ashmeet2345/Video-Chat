package com.videochat.controller;

import com.videochat.model.CallHistory;
import com.videochat.model.CallStatus;
import com.videochat.model.User;
import com.videochat.service.CallHistoryService;
import com.videochat.service.UserService;
import com.videochat.service.WebSocketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/calls")
@RequiredArgsConstructor
public class CallHistoryController {

    private final CallHistoryService callHistoryService;
    private final UserService userService;
    private final WebSocketService webSocketService;

    @GetMapping("/history")
    public ResponseEntity<List<CallHistory>> getCallHistory(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(callHistoryService.getCallHistoryForUser(currentUser));
    }

    @PostMapping("/initiate/{userId}")
    public ResponseEntity<CallHistory> initiateCall(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long userId) {
        User receiver = userService.findById(userId);
        CallHistory callHistory = callHistoryService.createCallRecord(currentUser, receiver);

        // Notify the receiver about incoming call
        webSocketService.notifyIncomingCall(
                userId,
                callHistory.getId(),
                userService.getCurrentUser(currentUser)
        );

        return ResponseEntity.ok(callHistory);
    }

    @PutMapping("/{callId}/accept")
    public ResponseEntity<CallHistory> acceptCall(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long callId) {
        CallHistory callHistory = callHistoryService.updateCallStatus(callId, CallStatus.ANSWERED);

        // Notify the caller that call is accepted
        webSocketService.notifyCallAccepted(
                callHistory.getCaller().getId(),
                callId
        );

        return ResponseEntity.ok(callHistory);
    }

    @PutMapping("/{callId}/reject")
    public ResponseEntity<CallHistory> rejectCall(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long callId) {
        CallHistory callHistory = callHistoryService.updateCallStatus(callId, CallStatus.REJECTED);

        // Notify the caller that call is rejected
        webSocketService.notifyCallRejected(
                callHistory.getCaller().getId(),
                callId
        );

        return ResponseEntity.ok(callHistory);
    }

    @PutMapping("/{callId}/end")
    public ResponseEntity<CallHistory> endCall(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long callId) {
        CallHistory callHistory = callHistoryService.endCall(callId);

        // Notify the other party that call has ended
        Long otherUserId = currentUser.getId().equals(callHistory.getCaller().getId()) ?
                callHistory.getReceiver().getId() : callHistory.getCaller().getId();

        webSocketService.notifyCallEnded(otherUserId, callId);

        return ResponseEntity.ok(callHistory);
    }
}