package com.br.mamba_wedding.guests.infrastructure;

import com.br.mamba_wedding.guests.domain.Guest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GuestRepository extends JpaRepository<Guest, Long> {

    Optional<Guest> findByRsvpCode(String rsvpCode);

    boolean existsByRsvpCode(String rsvpCode);
}
