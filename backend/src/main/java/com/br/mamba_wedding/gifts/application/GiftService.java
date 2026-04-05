package com.br.mamba_wedding.gifts.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.scheduling.annotation.Scheduled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.br.mamba_wedding.common.exception.NotFoundException;
import com.br.mamba_wedding.gifts.domain.Gift;
import com.br.mamba_wedding.gifts.domain.GiftTransaction;
import com.br.mamba_wedding.gifts.domain.TransactionStatus;
import com.br.mamba_wedding.gifts.infrastructure.GiftRepository;
import com.br.mamba_wedding.gifts.infrastructure.GiftTransactionRepository;
import com.br.mamba_wedding.payment.application.PaymentGateway;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class GiftService {
    private final GiftRepository giftRepository;
    private final GiftTransactionRepository giftTransactionRepository;
    private final PaymentGateway paymentGateway;

    private static final Logger log = LoggerFactory.getLogger(GiftService.class);

    public GiftService(GiftRepository giftRepository, GiftTransactionRepository giftTransactionRepository, PaymentGateway paymentGateway) {
        this.giftRepository = giftRepository;
        this.giftTransactionRepository = giftTransactionRepository;
        this.paymentGateway = paymentGateway;
    }

    public List<Gift> listAll() {
        List<Gift> gifts = giftRepository.findAll();

        return gifts;
    }

    public Gift findById(Long giftId){
        Gift gift = giftRepository.findById(giftId)
            .orElseThrow(() -> new NotFoundException("Presente não encontrado"));;
        return gift;
    }

    @Transactional
    public void reserve(Long giftId, String reservedBy, int quotas){
        Gift gift = giftRepository.findById(giftId)
                .orElseThrow(() -> new NotFoundException("Presente não encontrado"));

        if (quotas > gift.getAvailableQuotas()){
            throw new IllegalStateException(
                "Quantidade indisponível. Restam apenas " + gift.getAvailableQuotas() + " cotas para esse presente."
            );
        }

        GiftTransaction transaction = GiftTransaction.builder()
            .gift(gift)
            .guestName(reservedBy)
            .numberQuotas(quotas)
            .status(TransactionStatus.RESERVED)
            .reservedAt(LocalDateTime.now())
            .reservedUntil(LocalDateTime.now().plusHours(6))
            .build();

        gift.getTransactions().add(transaction);
        giftRepository.save(gift);
    }

    @Transactional
    public void cancelReserve(Long giftId, String guestName){
        Gift gift = giftRepository.findById(giftId)
                .orElseThrow(() -> new NotFoundException("Presente não encontrado"));
        
        GiftTransaction transaction = gift.getTransactions().stream()
            .filter(t -> t.getGuestName().equals(guestName) && t.getStatus() == TransactionStatus.RESERVED)
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("Você não possui uma reserva ativa deste presente para cancelar."));

            transaction.setStatus(TransactionStatus.CANCELED);
            giftRepository.save(gift);
        }

    @Transactional
    public void buy(Long giftId, String guestName){
        Gift gift = giftRepository.findById(giftId)
                .orElseThrow(() -> new NotFoundException("Presente não encontrado"));

        GiftTransaction transaction = gift.getTransactions().stream()
            .filter(t -> t.getGuestName().equals(guestName) && t.getStatus() == TransactionStatus.RESERVED)
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("Nenhuma reserva encontrada. Por favor, reserve as cotas antes de pagar."));

        BigDecimal quotaValue = gift.getValue().divide(new BigDecimal(gift.getTotalQuotas()), 2, RoundingMode.HALF_UP);
        BigDecimal valueToPay = quotaValue.multiply(new BigDecimal(transaction.getNumberQuotas()));

        paymentGateway.processPayment(transaction, valueToPay);

        // Sem exceções --> Pagamento feito
        transaction.setStatus(TransactionStatus.PURCHASED);
        transaction.setPurchasedAt(LocalDateTime.now());

        transaction.setReservedUntil(null);
        giftRepository.save(gift);
    }

    @Transactional
    @Scheduled(fixedRate = 60000) // 1 minuto
    public void clearExpiredReservations(){
        LocalDateTime now = LocalDateTime.now();

        List<GiftTransaction> expired = giftTransactionRepository.findByStatusAndReservedUntilBefore(TransactionStatus.RESERVED, now);

        if (!expired.isEmpty()){
            log.info("Foram encontradas {} transações com reservas expiradas. Cancelando reservas...", expired.size());
        }

        for (GiftTransaction transaction : expired){
            transaction.setStatus(TransactionStatus.CANCELED);
        }

        giftTransactionRepository.saveAll(expired);
    }
}
