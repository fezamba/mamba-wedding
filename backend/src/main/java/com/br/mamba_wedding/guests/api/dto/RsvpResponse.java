package com.br.mamba_wedding.guests.api.dto;

import com.br.mamba_wedding.guests.domain.GuestStatus;
public record RsvpResponse(
        String nomeCompleto,
        String codigoConvite,
        GuestStatus statusConvite
) {}
