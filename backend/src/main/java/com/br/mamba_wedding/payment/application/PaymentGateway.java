package com.br.mamba_wedding.payment.application;

import java.math.BigDecimal;

import com.br.mamba_wedding.gifts.domain.GiftTransaction;

public interface PaymentGateway {
    void processPayment(GiftTransaction transaction, BigDecimal valueToPay);
}
