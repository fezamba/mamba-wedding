package com.br.mamba_wedding.gifts.infrastructure;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.br.mamba_wedding.gifts.domain.Gift;
import com.br.mamba_wedding.gifts.domain.GiftStatus;

public interface GiftRepository extends JpaRepository<Gift, Long> {
    List<Gift> findByStatusAndReservadoAteBefore(GiftStatus status, LocalDateTime data);
}
