package com.br.mamba_wedding.guests.application;

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
    public RsvpResponse lookup(String codigoConvite) {
        Guest guest = guestRepository.findByCodigoConvite(codigoConvite)
                .orElseThrow(GuestNotFoundException::new);

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
        Guest guest = guestRepository.findByCodigoConvite(codigoConvite)
                .orElseThrow(GuestNotFoundException::new);

        guest.setStatusConvite(GuestStatus.CONFIRMADO);
        guest.setRsvpEm(LocalDateTime.now());
        
        guest.setEmail(email != null && email.trim().isEmpty() ? null : email);
        guest.setTelefone(telefone != null && telefone.trim().isEmpty() ? null : telefone);
        guest.setObservacoes(observacoes != null && observacoes.trim().isEmpty() ? null : observacoes);

        guestRepository.save(guest);
    }

    @Transactional
    public void decline(String codigoConvite, String email, String telefone, String observacoes) {
        Guest guest = guestRepository.findByCodigoConvite(codigoConvite)
                .orElseThrow(GuestNotFoundException::new);

        guest.setStatusConvite(GuestStatus.RECUSADO);
        guest.setRsvpEm(LocalDateTime.now());

        guest.setEmail(email != null && email.trim().isEmpty() ? null : email);
        guest.setTelefone(telefone != null && telefone.trim().isEmpty() ? null : telefone);
        guest.setObservacoes(observacoes != null && observacoes.trim().isEmpty() ? null : observacoes);

        guestRepository.save(guest);
    }
}