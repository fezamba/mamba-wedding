package com.br.mamba_wedding.gifts.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record ReserveRequest(
    @NotBlank(message = "A quantidade de cotas é obrigatória")
    @Min(value = 1, message = "Você deve reservar no mínimo 1 cota")
    Integer cotas
) {}
