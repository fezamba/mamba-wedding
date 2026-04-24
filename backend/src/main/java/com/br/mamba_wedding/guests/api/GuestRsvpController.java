package com.br.mamba_wedding.guests.api;


import com.br.mamba_wedding.config.security.PublicEndpointRateLimiter;
import com.br.mamba_wedding.guests.api.dto.RsvpActionRequest;
import com.br.mamba_wedding.guests.api.dto.RsvpLookupRequest;
import com.br.mamba_wedding.guests.api.dto.RsvpResponse;
import com.br.mamba_wedding.guests.application.GuestRsvpService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequestMapping("/api/rsvp")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class GuestRsvpController {

    private final GuestRsvpService guestRsvpService;
    private final PublicEndpointRateLimiter rateLimiter;

    @PostMapping("/lookup")
    public ResponseEntity<RsvpResponse> lookup(@Valid @RequestBody RsvpLookupRequest request, HttpServletRequest httpServletRequest) {
        rateLimiter.assertAllowed(httpServletRequest, "rsvp-lookup", request.rsvpCode(), 20, Duration.ofMinutes(1));
        return ResponseEntity.ok(guestRsvpService.lookup(request.rsvpCode()));
    }

    @PostMapping("/confirm")
    public ResponseEntity<Void> confirm(@Valid @RequestBody RsvpActionRequest request, HttpServletRequest httpServletRequest) {
        rateLimiter.assertAllowed(httpServletRequest, "rsvp-confirm", request.rsvpCode(), 10, Duration.ofMinutes(1));
        guestRsvpService.confirm(
                request.rsvpCode(),
                request.email(),
                request.phone(),
                request.notes()
        );
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/decline")
    public ResponseEntity<Void> decline(@Valid @RequestBody RsvpActionRequest request, HttpServletRequest httpServletRequest) {
        rateLimiter.assertAllowed(httpServletRequest, "rsvp-decline", request.rsvpCode(), 10, Duration.ofMinutes(1));
        guestRsvpService.decline(
                request.rsvpCode(),
                request.email(),
                request.phone(),
                request.notes()
        );
        return ResponseEntity.noContent().build();
    }
}