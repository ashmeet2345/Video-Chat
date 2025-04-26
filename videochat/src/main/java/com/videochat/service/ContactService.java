package com.videochat.service;

import com.videochat.dto.request.ContactRequest;
import com.videochat.dto.response.ContactResponse;
import com.videochat.exception.ResourceNotFoundException;
import com.videochat.model.Contact;
import com.videochat.model.ContactDirection;
import com.videochat.model.ContactStatus;
import com.videochat.model.User;
import com.videochat.repository.ContactRepository;
import com.videochat.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContactService {

    private final ContactRepository contactRepository;
    private final UserRepository userRepository;

    public List<ContactResponse> getContactsForUser(User currentUser) {
        List<Contact> contacts = contactRepository.findByOwner(currentUser);
        return contacts.stream()
                .map(this::mapToContactResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ContactResponse addContact(User currentUser, ContactRequest request) {
        Long contactUserId = request.getUserId();

        // Check if user exists
        User contactUser = userRepository.findById(contactUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + contactUserId));

        // Check if it's not the current user
        if (currentUser.getId().equals(contactUserId)) {
            throw new IllegalArgumentException("You cannot add yourself as a contact");
        }

        // Check if contact already exists
        if (contactRepository.existsByOwnerAndContactUser(currentUser, contactUser)) {
            throw new IllegalArgumentException("Contact already exists");
        }

        // Create new contact for current user (outgoing)
        Contact outgoingContact = Contact.builder()
                .owner(currentUser)
                .contactUser(contactUser)
                .status(ContactStatus.PENDING)
                .direction(ContactDirection.OUTGOING)
                .build();

        // Create reverse contact for the other user (incoming)
        Contact incomingContact = Contact.builder()
                .owner(contactUser)
                .contactUser(currentUser)
                .status(ContactStatus.PENDING)
                .direction(ContactDirection.INCOMING)
                .build();

        contactRepository.save(outgoingContact);
        contactRepository.save(incomingContact);

        return mapToContactResponse(outgoingContact);
    }

    @Transactional
    public ContactResponse acceptContact(User currentUser, Long contactId) {
        Contact contact = contactRepository.findById(contactId)
                .orElseThrow(() -> new ResourceNotFoundException("Contact not found with id: " + contactId));

        // Verify ownership
        if (!contact.getOwner().getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("You don't have permission to accept this contact");
        }

        // Check if it's an incoming contact
        if (contact.getDirection() != ContactDirection.INCOMING) {
            throw new IllegalArgumentException("Can only accept incoming contact requests");
        }

        // Update status to accepted
        contact.setStatus(ContactStatus.ACCEPTED);
        contactRepository.save(contact);

        // Update reverse contact
        Contact reverseContact = contactRepository.findByOwnerAndContactUser(contact.getContactUser(), currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Reverse contact not found"));

        reverseContact.setStatus(ContactStatus.ACCEPTED);
        contactRepository.save(reverseContact);

        return mapToContactResponse(contact);
    }

    @Transactional
    public ContactResponse rejectContact(User currentUser, Long contactId) {
        Contact contact = contactRepository.findById(contactId)
                .orElseThrow(() -> new ResourceNotFoundException("Contact not found with id: " + contactId));

        // Verify ownership
        if (!contact.getOwner().getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("You don't have permission to reject this contact");
        }

        // Check if it's an incoming contact
        if (contact.getDirection() != ContactDirection.INCOMING) {
            throw new IllegalArgumentException("Can only reject incoming contact requests");
        }

        // Update status to rejected
        contact.setStatus(ContactStatus.REJECTED);
        contactRepository.save(contact);

        // Update reverse contact
        Contact reverseContact = contactRepository.findByOwnerAndContactUser(contact.getContactUser(), currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Reverse contact not found"));

        reverseContact.setStatus(ContactStatus.REJECTED);
        contactRepository.save(reverseContact);

        return mapToContactResponse(contact);
    }

    @Transactional
    public void removeContact(User currentUser, Long contactId) {
        Contact contact = contactRepository.findById(contactId)
                .orElseThrow(() -> new ResourceNotFoundException("Contact not found with id: " + contactId));

        // Verify ownership
        if (!contact.getOwner().getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("You don't have permission to remove this contact");
        }

        // Find reverse contact
        Contact reverseContact = contactRepository.findByOwnerAndContactUser(contact.getContactUser(), currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Reverse contact not found"));

        // Delete both contacts
        contactRepository.delete(contact);
        contactRepository.delete(reverseContact);
    }

    public List<ContactResponse> getPendingContacts(User currentUser) {
        List<Contact> contacts = contactRepository.findByOwner(currentUser);
        return contacts.stream()
                .filter(contact -> contact.getStatus() == ContactStatus.PENDING)
                .map(this::mapToContactResponse)
                .collect(Collectors.toList());
    }

    private ContactResponse mapToContactResponse(Contact contact) {
        return ContactResponse.builder()
                .id(contact.getId())
                .userId(contact.getContactUser().getId())
                .username(contact.getContactUser().getUsername())
                .email(contact.getContactUser().getEmail())
                .status(contact.getStatus())
                .direction(contact.getDirection())
                .build();
    }
}