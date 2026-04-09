package com.br.mamba_wedding.guests.api.dto;

import com.br.mamba_wedding.guests.domain.GuestSide;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record GuestCreate (

        @NotBlank
        @Size(max = 120)
        String fullName,

        // TODO: Gerar automaticamente o code com base no nome do convidado
        @NotBlank
        @Size(min = 3, max = 32)
        String rsvpCode,

        @NotNull
        GuestSide side,

        @NotBlank(message = "Email é obrigatório")
        @Size(max = 120)
        @Email
        String email,

        @NotBlank(message = "Telefone é obrigatório")
        @Pattern(regexp = "^\\d{11}$", message = "Telefone inválido")
        String phone

) {}