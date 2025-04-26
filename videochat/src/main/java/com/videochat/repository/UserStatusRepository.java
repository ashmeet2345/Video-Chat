package com.videochat.repository;

import com.videochat.model.UserStatus;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserStatusRepository extends CrudRepository<UserStatus, String> {

    Optional<UserStatus> findByUserId(Long userId);

    Iterable<UserStatus> findByOnline(boolean online);
}