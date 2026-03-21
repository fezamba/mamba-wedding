package com.br.mamba_wedding.admin.api;

import com.br.mamba_wedding.config.security.TokenService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/admin/auth")
@CrossOrigin(origins = "*")
public class AdminAuthController {

    private final TokenService tokenService;
    private final GoogleIdTokenVerifier verifier;
    private final List<String> authorizedEmails;

    public AdminAuthController(
            TokenService tokenService,
            @Value("${api.security.google.client-id}") String clientId,
            @Value("${api.security.admin.emails}") String adminEmails
    ) {
        this.tokenService = tokenService;
        this.authorizedEmails = Arrays.asList(adminEmails.split(","));
        
        this.verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                .setAudience(Collections.singletonList(clientId))
                .build();
    }

    public record GoogleLoginRequest(String googleToken) {}
    public record LoginResponse(String token) {}

    @PostMapping("/google")
    public ResponseEntity<?> authenticateGoogle(@RequestBody GoogleLoginRequest request) {
        try {
            GoogleIdToken idToken = verifier.verify(request.googleToken());
            
            if (idToken != null) {
                GoogleIdToken.Payload payload = idToken.getPayload();
                String email = payload.getEmail();

                if (authorizedEmails.contains(email)) {
                    String internalToken = tokenService.generateToken(email, "ROLE_ADMIN");
                    return ResponseEntity.ok(new LoginResponse(internalToken));
                } else {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied. Unauthorized email.");
                }
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired Google token.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal error while processing authentication");
        }
    }
}