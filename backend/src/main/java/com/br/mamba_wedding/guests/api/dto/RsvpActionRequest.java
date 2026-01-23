package com.br.mamba_wedding.guests.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Email;

public record RsvpActionRequest(
    @NotBlank()
    @Size(min = 3, max = 32)
    String codigoConvite,

    // FIXME: Incluir números de outros países no futuro
    @NotBlank(message = "Telefone é obrigatório")
    @Pattern(regexp = "^\\d{11}$", message = "Telefone inválido")
    String telefone,

    @Size(max = 120)
    @Email
    String email,

    @Size(max = 255)
    String observacoes
) {}
