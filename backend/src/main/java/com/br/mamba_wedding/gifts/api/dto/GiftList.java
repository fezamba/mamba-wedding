package com.br.mamba_wedding.gifts.api.dto;

import com.br.mamba_wedding.gifts.domain.Gift;
import java.math.BigDecimal;
import java.math.RoundingMode;

public record GiftList(
        Long id,
        String nome,
        String descricao,
        BigDecimal valorTotal,
        BigDecimal valorCota,
        Integer cotasTotais,
        Integer cotasDisponiveis,
        String imagemUrl,
        String linkCompra,
        boolean esgotado
) {
    public static GiftList from(Gift gift) {
        BigDecimal valorCota = gift.getValor().divide(new BigDecimal(gift.getCotasTotais()), 2, RoundingMode.HALF_UP);
        
        return new GiftList(
                gift.getId(),
                gift.getNome(),
                gift.getDescricao(),
                gift.getValor(),
                valorCota,
                gift.getCotasTotais(),
                gift.getCotasDisponiveis(),
                gift.getImagemUrl(),
                gift.getLinkCompra(),
                gift.isEsgotado()
        );
    }
}