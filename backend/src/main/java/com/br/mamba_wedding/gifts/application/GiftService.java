package com.br.mamba_wedding.gifts.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.scheduling.annotation.Scheduled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.br.mamba_wedding.common.exception.NotFoundException;
import com.br.mamba_wedding.gifts.domain.Gift;
import com.br.mamba_wedding.gifts.domain.GiftStatus;
import com.br.mamba_wedding.gifts.infrastructure.GiftRepository;
import com.br.mamba_wedding.payment.application.PaymentGateway;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class GiftService {
    private final GiftRepository giftRepository;
    private final PaymentGateway paymentGateway;

    private static final Logger log = LoggerFactory.getLogger(GiftService.class);

    public GiftService(GiftRepository giftRepository, PaymentGateway paymentGateway) {
        this.giftRepository = giftRepository;
        this.paymentGateway = paymentGateway;
    }

    public List<Gift> listAll() {
        List<Gift> gifts = giftRepository.findAll();

        return gifts;
    }

    public Gift buscarPorId(Long giftId){
        Gift gift = giftRepository.findById(giftId)
            .orElseThrow(() -> new NotFoundException("Presente não encontrado"));;
        return gift;
    }

    @Transactional
    public void reservar(Long giftId, String reservadoPor){
        Gift gift = giftRepository.findById(giftId)
                .orElseThrow(() -> new NotFoundException("Presente não encontrado"));

        if (gift.getStatus() != GiftStatus.DISPONIVEL) {
            throw new IllegalStateException("Presente já reservado/comprado");
        }

        gift.setStatus(GiftStatus.RESERVADO);
        gift.setReservadoPor(reservadoPor);
        gift.setReservadoEm(LocalDateTime.now());
        giftRepository.save(gift);
    }

    @Transactional
    public void cancelarReserva(Long giftId, String guestName){
        Gift gift = giftRepository.findById(giftId)
                .orElseThrow(() -> new NotFoundException("Presente não encontrado"));
        
        if (gift.getStatus() != GiftStatus.RESERVADO) {
            throw new IllegalStateException("Apenas presentes reservados podem ter a reserva cancelada.");
        }

        if (gift.getReservadoPor() == null || !gift.getReservadoPor().equals(guestName)){
            throw new IllegalStateException("Você não tem permissão para cancelar uma reserva feita por outra pessoa");
        }

        gift.setStatus(GiftStatus.DISPONIVEL);
        gift.setReservadoPor(null);
        gift.setReservadoEm(null);
        giftRepository.save(gift);
    }

    @Transactional
    public void comprar(Long giftId, String guestName){
        Gift gift = giftRepository.findById(giftId)
                .orElseThrow(() -> new NotFoundException("Presente não encontrado"));

        if (gift.getStatus() != GiftStatus.COMPRADO) {
            throw new IllegalStateException("Presente já foi comprado");
        }

        paymentGateway.processPayment(gift, guestName);

        // Without exceptions -> Payment discharged
        gift.setStatus(GiftStatus.COMPRADO);
        gift.setCompradoPor(guestName);

        gift.setReservadoPor(null);
        gift.setReservadoEm(null);

        giftRepository.save(gift);
    }

    @Transactional
    @Scheduled(fixedRate = 60000) // 1 minute
    public void limparReservasExpiradas(){
        LocalDateTime now = LocalDateTime.now();

        List<Gift> expired = giftRepository.findByStatusAndReservadoAteBefore(GiftStatus.RESERVADO, now);

        if (!expired.isEmpty()){
            log.info("Found {} presents with expired reserve. Canceling reserves...", expired.size());
        }

        for (Gift gift : expired){
            gift.setStatus(GiftStatus.DISPONIVEL);
            gift.setReservadoPor(null);
            gift.setReservadoEm(null);
            gift.setReservadoAte(null);
        }
    }
}
