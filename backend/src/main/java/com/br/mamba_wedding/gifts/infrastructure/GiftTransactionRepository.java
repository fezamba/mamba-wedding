package com.br.mamba_wedding.gifts.infrastructure;

import com.br.mamba_wedding.gifts.domain.GiftTransaction;
import com.br.mamba_wedding.gifts.domain.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface GiftTransactionRepository extends JpaRepository<GiftTransaction, Long> {
    
    List<GiftTransaction> findByStatusAndReservadoAteBefore(TransactionStatus status, LocalDateTime data);
}