package com.br.mamba_wedding.guests.application;

import com.br.mamba_wedding.common.exception.NotFoundException;
import com.br.mamba_wedding.guests.api.dto.GuestCreate;
import com.br.mamba_wedding.guests.api.dto.GuestCreated;
import com.br.mamba_wedding.guests.api.dto.RsvpResponse;
import com.br.mamba_wedding.guests.domain.Guest;
import com.br.mamba_wedding.guests.domain.GuestNotFoundException;
import com.br.mamba_wedding.guests.domain.GuestStatus;
import com.br.mamba_wedding.guests.infrastructure.GuestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class GuestRsvpService {

    private final GuestRepository guestRepository;

    @Transactional(readOnly = true)
    public RsvpResponse lookup(String rsvpCode) {
        Guest guest = guestRepository.findByRsvpCode(rsvpCode)
                .orElseThrow(GuestNotFoundException::new);

        return new RsvpResponse(
                guest.getFullName(),
                guest.getRsvpCode(),
                guest.getRsvpStatus(),
                guest.getEmail(),
                guest.getPhone(),
                guest.getNotes()
        );
    }

    @Transactional
    public void confirm(String rsvpCode, String email, String phone, String notes) {
        Guest guest = guestRepository.findByRsvpCode(rsvpCode)
                .orElseThrow(GuestNotFoundException::new);

        guest.setRsvpStatus(GuestStatus.CONFIRMED);
        guest.setRsvpBy(LocalDateTime.now());
        
        guest.setEmail(email != null && email.trim().isEmpty() ? null : email);
        guest.setPhone(phone != null && phone.trim().isEmpty() ? null : phone);
        guest.setNotes(notes != null && notes.trim().isEmpty() ? null : notes);

        guestRepository.save(guest);
    }

    @Transactional
    public void decline(String rsvpCode, String email, String phone, String notes) {
        Guest guest = guestRepository.findByRsvpCode(rsvpCode)
                .orElseThrow(GuestNotFoundException::new);

        guest.setRsvpStatus(GuestStatus.REJECTED);
        guest.setRsvpBy(LocalDateTime.now());

        guest.setEmail(email != null && email.trim().isEmpty() ? null : email);
        guest.setPhone(phone != null && phone.trim().isEmpty() ? null : phone);
        guest.setNotes(notes != null && notes.trim().isEmpty() ? null : notes);

        guestRepository.save(guest);
    }

    @Transactional
    public GuestCreated register(GuestCreate guestCreate) {

        String rsvpCode = rsvpCodeBuilder(guestCreate.fullName());
        Guest guest = Guest.builder()
            .fullName(guestCreate.fullName())
            .rsvpCode(rsvpCode)
            .rsvpStatus(GuestStatus.PENDING)
            .side(guestCreate.side())
            .email(guestCreate.email())
            .phone(guestCreate.phone())
            .build();
        
        Guest savedGuest = guestRepository.save(guest);
        GuestCreated guestCreated = new GuestCreated(savedGuest);

        return guestCreated;
    }

    @Transactional
    public void delete(Long guestId){
        Guest guest = guestRepository.findById(guestId)
                .orElseThrow(() -> new NotFoundException("Convidado não encontrado"));
        
        guestRepository.delete(guest);
    }

    public String rsvpCodeBuilder(String name){

        String initials = name.substring(0, 3).toUpperCase();
        String random4Digit = String.valueOf((int)(Math.random() * (9999 - 1000 + 1) + 1000));

        return initials + random4Digit;
    }
}