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

import java.time.LocalDateTime;
import java.util.List;

@Service
public class GiftService {
    private final GiftRepository giftRepository;

    private static final Logger log = LoggerFactory.getLogger(GiftService.class);

    public GiftService(GiftRepository giftRepository) {
        this.giftRepository = giftRepository;
    }

    public List<Gift> listAll() {
        List<Gift> gifts = giftRepository.findAll();

        // gifts.forEach(this::expirarSeNecessario);

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

        // expirarSeNecessario(gift);

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
        
        // expirarSeNecessario(gift);

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

    // provavelmente vai ter algo mais complexo aqui, vou receber uma confirmação do gateway de pagamento e aí sim disparar esse bebezinho, vou ver com calma depois
    @Transactional
    public void comprar(Long giftId){
        Gift gift = giftRepository.findById(giftId)
                .orElseThrow(() -> new NotFoundException("Presente não encontrado"));

        // expirarSeNecessario(gift);

        if (gift.getStatus() != GiftStatus.RESERVADO) {
            throw new IllegalStateException("Presente já reservado/comprado");
        }

        gift.setStatus(GiftStatus.COMPRADO);
        gift.setCompradoEm(LocalDateTime.now());
        giftRepository.save(gift);
    }

    @Transactional
    @Scheduled(fixedRate = 60000) // 1 minuto
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

    /* 
    private void expirarSeNecessario(Gift gift) {
        if (gift.getStatus() == GiftStatus.RESERVADO
            && gift.getReservadoAte() != null
            && gift.getReservadoAte().isBefore(LocalDateTime.now())) {
            gift.setStatus(GiftStatus.DISPONIVEL);
            gift.setReservadoPor(null);
            gift.setReservadoEm(null);
            gift.setReservadoAte(null);
        }
    }
    */
}
