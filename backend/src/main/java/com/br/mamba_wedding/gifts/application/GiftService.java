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

    public Gift buscarPorId(Long giftId){
        Gift gift = giftRepository.findById(giftId)
            .orElseThrow(() -> new NotFoundException("Presente não encontrado"));;
        return gift;
    }

    @Transactional
    public void reservar(Long giftId, String reservadoPor, int cotas){
        Gift gift = giftRepository.findById(giftId)
                .orElseThrow(() -> new NotFoundException("Presente não encontrado"));

        if (cotas > gift.getCotasDisponiveis()){
            throw new IllegalStateException(
                "Quantidade indisponível. Restam apenas " + gift.getCotasDisponiveis() + " cotas para esse presente."
            );
        }

        GiftTransaction transacao = GiftTransaction.builder()
            .gift(gift)
            .guestName(reservadoPor)
            .quantidadeCotas(cotas)
            .status(TransactionStatus.RESERVADO)
            .reservadoEm(LocalDateTime.now())
            .reservadoAte(LocalDateTime.now().plusHours(6))
            .build();

        gift.getTransacoes().add(transacao);
        giftRepository.save(gift);
    }

    @Transactional
    public void cancelarReserva(Long giftId, String guestName){
        Gift gift = giftRepository.findById(giftId)
                .orElseThrow(() -> new NotFoundException("Presente não encontrado"));
        
        GiftTransaction transacao = gift.getTransacoes().stream()
            .filter(t -> t.getGuestName().equals(guestName) && t.getStatus() == TransactionStatus.RESERVADO)
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("Você não possui uma reserva ativa deste presente para cancelar."));

            transacao.setStatus(TransactionStatus.CANCELADO);
            giftRepository.save(gift);
        }

    @Transactional
    public void comprar(Long giftId, String guestName){
        Gift gift = giftRepository.findById(giftId)
                .orElseThrow(() -> new NotFoundException("Presente não encontrado"));

        GiftTransaction transacao = gift.getTransacoes().stream()
            .filter(t -> t.getGuestName().equals(guestName) && t.getStatus() == TransactionStatus.RESERVADO)
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("Nenhuma reserva encontrada. Por favor, reserve as cotas antes de pagar."));

        BigDecimal valorCota = gift.getValor().divide(new BigDecimal(gift.getCotasTotais()), 2, RoundingMode.HALF_UP);
        BigDecimal valorAPagar = valorCota.multiply(new BigDecimal(transacao.getQuantidadeCotas()));

        paymentGateway.processPayment(transacao, valorAPagar);

        // Sem exceções --> Pagamento feito
        transacao.setStatus(TransactionStatus.COMPRADO);
        transacao.setCompradoEm(LocalDateTime.now());

        transacao.setReservadoAte(null);
        giftRepository.save(gift);
    }

    @Transactional
    @Scheduled(fixedRate = 60000) // 1 minute
    public void limparReservasExpiradas(){
        LocalDateTime agora = LocalDateTime.now();

        List<GiftTransaction> expiradas = giftTransactionRepository.findByStatusAndReservadoAteBefore(TransactionStatus.RESERVADO, agora);

        if (!expiradas.isEmpty()){
            log.info("Foram encontradas {} transações com reservas expiradas. Cancelando reservas...", expiradas.size());
        }

        for (GiftTransaction transacao : expiradas){
            transacao.setStatus(TransactionStatus.CANCELADO);
        }

        giftTransactionRepository.saveAll(expiradas);
    }
}
