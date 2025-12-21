package com.br.mamba_wedding.guests.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RsvpActionRequest (
    @NotBlank
    @Size(min = 3, max = 32)
    String codigoConvite,

    @Size(max = 120)
    String email,

    @Size(max = 30)
    String telefone,

    @Size(max = 255)
    String observacoes
) {}
