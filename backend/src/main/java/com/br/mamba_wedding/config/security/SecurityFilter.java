package com.br.mamba_wedding.config.security;

import com.br.mamba_wedding.guests.domain.Guest;
import com.br.mamba_wedding.guests.infrastructure.GuestRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final GuestRepository guestRepository;

    public SecurityFilter(TokenService tokenService, GuestRepository guestRepository) {
        this.tokenService = tokenService;
        this.guestRepository = guestRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var token = this.recoverToken(request);
        
        if (token != null) {
            var codigoConvite = tokenService.validateToken(token);
            
            if (!codigoConvite.isEmpty()) {
                Guest guest = guestRepository.findByCodigoConvite(codigoConvite)
                        .orElseThrow(() -> new RuntimeException("Convidado não encontrado"));

                // Collections.emptyList() --> not using Roles
                var authentication = new UsernamePasswordAuthenticationToken(guest, null, Collections.emptyList());
                
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        filterChain.doFilter(request, response);
    }

    private String recoverToken(HttpServletRequest request) {
        var authHeader = request.getHeader("Authorization");
        if (authHeader == null) return null;
        return authHeader.replace("Bearer ", "");
    }
}