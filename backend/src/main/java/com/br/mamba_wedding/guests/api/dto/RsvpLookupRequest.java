package com.br.mamba_wedding.guests.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RsvpLookupRequest(
        @NotBlank
        @Size(min = 3, max = 32)
        String codigoConvite
) {}