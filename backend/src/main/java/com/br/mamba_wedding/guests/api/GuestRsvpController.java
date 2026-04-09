package com.br.mamba_wedding.guests.api;


import com.br.mamba_wedding.guests.api.dto.GuestCreate;
import com.br.mamba_wedding.guests.api.dto.GuestCreated;
import com.br.mamba_wedding.guests.api.dto.RsvpActionRequest;
import com.br.mamba_wedding.guests.api.dto.RsvpLookupRequest;
import com.br.mamba_wedding.guests.api.dto.RsvpResponse;
import com.br.mamba_wedding.guests.application.GuestRsvpService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rsvp")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class GuestRsvpController {

    private final GuestRsvpService guestRsvpService;

    @PostMapping("/lookup")
    public ResponseEntity<RsvpResponse> lookup(@Valid @RequestBody RsvpLookupRequest request) {
        return ResponseEntity.ok(guestRsvpService.lookup(request.rsvpCode()));
    }

    @PostMapping("/confirm")
    public ResponseEntity<Void> confirm(@Valid @RequestBody RsvpActionRequest request) {
        guestRsvpService.confirm(
                request.rsvpCode(),
                request.email(),
                request.phone(),
                request.notes()
        );
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/decline")
    public ResponseEntity<Void> decline(@Valid @RequestBody RsvpActionRequest request) {
        guestRsvpService.decline(
                request.rsvpCode(),
                request.email(),
                request.phone(),
                request.notes()
        );
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/register")
    public ResponseEntity<GuestCreated> registerGuest(@Valid @RequestBody GuestCreate guestCreate){
        GuestCreated response = guestRsvpService.register(guestCreate);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}/delete")
    public ResponseEntity<Void> deleteGuest(@PathVariable Long id){
        guestRsvpService.delete(id);
        return ResponseEntity.noContent().build();
    }
}