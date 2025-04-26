package com.videochat.repository;

import com.videochat.model.Contact;
import com.videochat.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {
    List<Contact> findByOwner(User owner);

    Optional<Contact> findByOwnerAndContactUser(User owner, User contactUser);

    boolean existsByOwnerAndContactUser(User owner, User contactUser);
}