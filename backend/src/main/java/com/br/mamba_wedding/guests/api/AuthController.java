package com.br.mamba_wedding.guests.api;

import com.br.mamba_wedding.config.security.TokenService;
import com.br.mamba_wedding.guests.domain.Guest;
import com.br.mamba_wedding.guests.domain.GuestNotFoundException;
import com.br.mamba_wedding.guests.infrastructure.GuestRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final GuestRepository guestRepository;
    private final TokenService tokenService;

    public AuthController(GuestRepository guestRepository, TokenService tokenService) {
        this.guestRepository = guestRepository;
        this.tokenService = tokenService;
    }

    public record LoginRequest(String codigoConvite) {}
    public record LoginResponse(String token) {}

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        Guest guest = guestRepository.findByCodigoConvite(request.codigoConvite())
                .orElseThrow(() -> new GuestNotFoundException());

        String token = tokenService.generateToken(guest);

        return ResponseEntity.ok(new LoginResponse(token));
    }
}