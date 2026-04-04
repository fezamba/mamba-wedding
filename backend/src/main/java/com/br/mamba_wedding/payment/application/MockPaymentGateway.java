package com.br.mamba_wedding.payment.application;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.br.mamba_wedding.gifts.domain.GiftTransaction;

@Service
public class MockPaymentGateway implements PaymentGateway {
    
    private static final Logger log = LoggerFactory.getLogger(MockPaymentGateway.class);

    @Override
    public void processPayment(GiftTransaction transacao, BigDecimal valorAPagar) {
        log.info("Inicializando Mock");
        log.info("Convidado: {}", transacao.getGuestName());
        log.info("Presente: {} | Cotas: {} | Valor total: R$ {}", 
            transacao.getGift().getNome(), 
            transacao.getQuantidadeCotas(), 
            valorAPagar);

        try {
            // Simulando latência de requisição HTTP (1.5 segundos)
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Simulação de pagamento falhou.", e);
        }

        log.info("Simulação de pagamento aprovado com sucesso!");
    }
}
