package com.br.mamba_wedding.payment.application;

import com.br.mamba_wedding.gifts.domain.Gift;

public interface PaymentGateway {
    void processPayment(Gift gift, String guestName);
}
