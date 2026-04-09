package com.br.mamba_wedding.gifts.api.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record GiftCreate (

        @NotBlank
        String name,

        @NotBlank
        String description,

        @NotNull
        @Positive
        BigDecimal value,

        @NotNull
        @Positive
        Integer totalQuotas,

        @NotBlank
        String imageUrl,

        @NotBlank
        String purchaseLink
) {}