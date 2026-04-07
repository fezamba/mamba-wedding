package com.br.mamba_wedding.gifts.domain;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class GiftTest {

    @Test
    void shouldCalculateAvailableQuotasCorrectly() {
        // Arrange
        Gift gift = Gift.builder()
                .name("Geladeira")
                .value(new BigDecimal("2500.00"))
                .totalQuotas(5)
                .transactions(new ArrayList<>())
                .build();

        // Simulando uma reserva ativa (2 cotas)
        gift.getTransactions().add(GiftTransaction.builder()
                .status(TransactionStatus.RESERVED)
                .numberQuotas(2)
                .build());

        // Simulando uma compra confirmada (1 cota)
        gift.getTransactions().add(GiftTransaction.builder()
                .status(TransactionStatus.PURCHASED)
                .numberQuotas(1)
                .build());

        // Simulando uma reserva cancelada (2 cotas) - não deve afetar o total
        gift.getTransactions().add(GiftTransaction.builder()
                .status(TransactionStatus.CANCELED)
                .numberQuotas(2)
                .build());

        // Act
        Integer available = gift.getAvailableQuotas();

        // Assert: 5 (total) - 2 (reservado) - 1 (comprado) = 2 disponíveis
        assertEquals(2, available);
        assertFalse(gift.isSoldOut());
    }

    @Test
    void shouldReturnSoldOutWhenNoQuotasAvailable() {
        // Arrange
        Gift gift = Gift.builder()
                .totalQuotas(2)
                .transactions(new ArrayList<>())
                .build();

        gift.getTransactions().add(GiftTransaction.builder()
                .status(TransactionStatus.PURCHASED)
                .numberQuotas(2)
                .build());

        // Act & Assert
        assertEquals(0, gift.getAvailableQuotas());
        assertTrue(gift.isSoldOut());
    }
}