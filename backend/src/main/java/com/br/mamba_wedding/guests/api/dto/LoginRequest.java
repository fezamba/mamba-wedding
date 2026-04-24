package com.br.mamba_wedding.guests.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @NotBlank(message = "Código de convidado é obrigatório")
        @Size(min = 3, max = 32, message = "Código de convidado inválido")
        String rsvpCode
) {}