package com.br.mamba_wedding.guests.api;


import com.br.mamba_wedding.guests.api.dto.RsvpLookupRequest;
import com.br.mamba_wedding.guests.api.dto.RsvpResponse;
import com.br.mamba_wedding.guests.application.GuestRsvpService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rsvp")
@RequiredArgsConstructor
public class GuestRsvpController {

    private final GuestRsvpService guestRsvpService;

    @PostMapping("/lookup")
    public ResponseEntity<RsvpResponse> lookup(@Valid @RequestBody RsvpLookupRequest request) {
        return ResponseEntity.ok(guestRsvpService.lookup(request.codigoConvite()));
    }

    @PostMapping("/confirm")
    public ResponseEntity<Void> confirm(
            @RequestParam String codigoConvite,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String telefone,
            @RequestParam(required = false) @Size(max = 255) String observacoes
    ) {
        guestRsvpService.confirm(codigoConvite, email, telefone, observacoes);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/decline")
    public ResponseEntity<Void> decline(
            @RequestParam String codigoConvite,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String telefone,
            @RequestParam(required = false) @Size(max = 255) String observacoes
    ) {
        guestRsvpService.decline(codigoConvite, email, telefone, observacoes);
        return ResponseEntity.noContent().build();
    }
}
