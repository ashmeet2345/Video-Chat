package com.videochat.controller;

import com.videochat.dto.request.ContactRequest;
import com.videochat.dto.response.ContactResponse;
import com.videochat.model.User;
import com.videochat.service.ContactService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contacts")
@RequiredArgsConstructor
public class ContactController {

    private final ContactService contactService;

    @GetMapping
    public ResponseEntity<List<ContactResponse>> getContacts(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(contactService.getContactsForUser(currentUser));
    }

    @PostMapping
    public ResponseEntity<ContactResponse> addContact(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody ContactRequest request) {
        return ResponseEntity.ok(contactService.addContact(currentUser, request));
    }

    @PutMapping("/{contactId}/accept")
    public ResponseEntity<ContactResponse> acceptContact(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long contactId) {
        return ResponseEntity.ok(contactService.acceptContact(currentUser, contactId));
    }

    @PutMapping("/{contactId}/reject")
    public ResponseEntity<ContactResponse> rejectContact(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long contactId) {
        return ResponseEntity.ok(contactService.rejectContact(currentUser, contactId));
    }

    @DeleteMapping("/{contactId}")
    public ResponseEntity<Void> removeContact(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long contactId) {
        contactService.removeContact(currentUser, contactId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/pending")
    public ResponseEntity<List<ContactResponse>> getPendingContacts(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(contactService.getPendingContacts(currentUser));
    }
}