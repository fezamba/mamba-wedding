package com.br.mamba_wedding.guests.application;

import com.br.mamba_wedding.guests.api.dto.RsvpResponse;
import com.br.mamba_wedding.guests.domain.Guest;
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
    public RsvpResponse lookup(String codigoConvite) {
        String normalizedCode = normalizeCodigoConvite(codigoConvite);
        Guest guest = guestRepository.findByCodigoConvite(normalizedCode)
                .orElseThrow(() -> new IllegalArgumentException("Código de convite inválido."));

        return new RsvpResponse(
                guest.getNomeCompleto(),
                guest.getCodigoConvite(),
                guest.getStatusConvite(),
                guest.getEmail(),
                guest.getTelefone(),
                guest.getObservacoes()
        );
    }

    @Transactional
    public void confirm(String codigoConvite, String email, String telefone, String observacoes) {
        String normalizedCode = normalizeCodigoConvite(codigoConvite);
        Guest guest = guestRepository.findByCodigoConvite(normalizedCode)
                .orElseThrow(() -> new IllegalArgumentException("Código de convite inválido."));

        guest.setStatusConvite(GuestStatus.CONFIRMADO);
        guest.setRsvpEm(LocalDateTime.now());

        if (email != null && !email.isBlank()) guest.setEmail(email);
        if (telefone != null && !telefone.isBlank()) guest.setTelefone(telefone);
        if (observacoes != null && !observacoes.isBlank()) guest.setObservacoes(observacoes);

        guestRepository.save(guest);
    }

    @Transactional
    public void decline(String codigoConvite, String email, String telefone, String observacoes) {
        String normalizedCode = normalizeCodigoConvite(codigoConvite);
        Guest guest = guestRepository.findByCodigoConvite(normalizedCode)
                .orElseThrow(() -> new IllegalArgumentException("Código de convite inválido."));

        guest.setStatusConvite(GuestStatus.RECUSADO);
        guest.setRsvpEm(LocalDateTime.now());

        if (email != null && !email.isBlank()) guest.setEmail(email);
        if (telefone != null && !telefone.isBlank()) guest.setTelefone(telefone);
        if (observacoes != null && !observacoes.isBlank()) guest.setObservacoes(observacoes);

        guestRepository.save(guest);
    }

    private String normalizeCodigoConvite(String codigoConvite){
        return codigoConvite == null ? null : codigoConvite.trim().toUpperCase();
    }
}
