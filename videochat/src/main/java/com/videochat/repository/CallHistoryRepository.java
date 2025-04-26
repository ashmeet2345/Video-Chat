package com.videochat.repository;

import com.videochat.model.CallHistory;
import com.videochat.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CallHistoryRepository extends JpaRepository<CallHistory, Long> {
    List<CallHistory> findByCallerOrReceiverOrderByStartTimeDesc(User caller, User receiver);

    List<CallHistory> findByCallerOrReceiverAndStartTimeBetween(
            User caller, User receiver, LocalDateTime startFrom, LocalDateTime startTo);
}