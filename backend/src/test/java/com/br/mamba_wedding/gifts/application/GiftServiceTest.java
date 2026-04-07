package com.br.mamba_wedding.gifts.application;

import com.br.mamba_wedding.gifts.domain.Gift;
import com.br.mamba_wedding.gifts.domain.GiftTransaction;
import com.br.mamba_wedding.gifts.domain.TransactionStatus;
import com.br.mamba_wedding.gifts.infrastructure.GiftRepository;
import com.br.mamba_wedding.gifts.infrastructure.GiftTransactionRepository;
import com.br.mamba_wedding.payment.application.PaymentGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GiftServiceTest {

    @Mock
    private GiftRepository giftRepository;

    @Mock
    private GiftTransactionRepository giftTransactionRepository;

    @Mock
    private PaymentGateway paymentGateway;

    @InjectMocks
    private GiftService giftService;

    private Gift sampleGift;

    @BeforeEach
    void setUp() {
        sampleGift = Gift.builder()
                .id(1L)
                .name("Televisão")
                .value(new BigDecimal("3000.00"))
                .totalQuotas(3)
                .transactions(new ArrayList<>())
                .build();
    }

    @Test
    void reserve_ShouldReserveQuotasSuccessfully() {
        // Arrange
        when(giftRepository.findById(1L)).thenReturn(Optional.of(sampleGift));

        // Act
        giftService.reserve(1L, "Convidado Teste", 2);

        // Assert
        assertEquals(1, sampleGift.getTransactions().size());
        GiftTransaction transaction = sampleGift.getTransactions().get(0);
        assertEquals("Convidado Teste", transaction.getGuestName());
        assertEquals(2, transaction.getNumberQuotas());
        assertEquals(TransactionStatus.RESERVED, transaction.getStatus());
        verify(giftRepository, times(1)).save(sampleGift);
    }

    @Test
    void reserve_ShouldThrowExceptionWhenQuotasExceedAvailable() {
        // Arrange
        when(giftRepository.findById(1L)).thenReturn(Optional.of(sampleGift));

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            giftService.reserve(1L, "Convidado Teste", 4); // Tentando reservar 4, mas só há 3
        });

        assertEquals("Quantidade indisponível. Restam apenas 3 cotas para esse presente.", exception.getMessage());
        verify(giftRepository, never()).save(any());
    }

    @Test
    void buy_ShouldProcessPaymentAndChangeStatus() {
        // Arrange
        GiftTransaction reservation = GiftTransaction.builder()
                .guestName("Convidado Teste")
                .numberQuotas(1)
                .status(TransactionStatus.RESERVED)
                .reservedUntil(LocalDateTime.now().plusHours(1))
                .build();
        sampleGift.getTransactions().add(reservation);

        when(giftRepository.findById(1L)).thenReturn(Optional.of(sampleGift));

        // Act
        giftService.buy(1L, "Convidado Teste");

        // Assert
        verify(paymentGateway, times(1)).processPayment(eq(reservation), any(BigDecimal.class));
        assertEquals(TransactionStatus.PURCHASED, reservation.getStatus());
        assertNotNull(reservation.getPurchasedAt());
        assertNull(reservation.getReservedUntil()); // O prazo de reserva deve ser limpo
        verify(giftRepository, times(1)).save(sampleGift);
    }

    @Test
    void buy_ShouldThrowExceptionWhenNoReservationFound() {
        // Arrange
        when(giftRepository.findById(1L)).thenReturn(Optional.of(sampleGift));
        // Nota: Não adicionamos nenhuma reserva ao sampleGift

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            giftService.buy(1L, "Convidado Sem Reserva");
        });

        assertEquals("Nenhuma reserva encontrada. Por favor, reserve as cotas antes de pagar.", exception.getMessage());
        verify(paymentGateway, never()).processPayment(any(), any());
    }

    @Test
    void clearExpiredReservations_ShouldCancelExpiredReservations() {
        // Arrange
        GiftTransaction expiredTransaction = GiftTransaction.builder()
                .status(TransactionStatus.RESERVED)
                .reservedUntil(LocalDateTime.now().minusMinutes(10)) // Expirou no passado
                .build();

        when(giftTransactionRepository.findByStatusAndReservedUntilBefore(
                eq(TransactionStatus.RESERVED), any(LocalDateTime.class)))
                .thenReturn(List.of(expiredTransaction));

        // Act
        giftService.clearExpiredReservations();

        // Assert
        assertEquals(TransactionStatus.CANCELED, expiredTransaction.getStatus());
        verify(giftTransactionRepository, times(1)).saveAll(anyList());
    }
}