package com.br.mamba_wedding.payment.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.br.mamba_wedding.gifts.domain.Gift;

@Service
public class MockPaymentGateway implements PaymentGateway {
    
    private static final Logger log = LoggerFactory.getLogger(MockPaymentGateway.class);

    @Override
    public void processPayment(Gift gift, String guestName) {
        log.info("Initializing Mock Payment");
        log.info("Guest: {}", guestName);
        log.info("Present: {} | Value: R$ {}", gift.getNome(), gift.getValor());

        try {
            // Simulate HTTP request latency (1.5 seconds)
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Payment simulation failed", e);
        }

        log.info("Payment simulation approved with success");
    }
}
