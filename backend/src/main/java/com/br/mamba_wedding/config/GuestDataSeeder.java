package com.br.mamba_wedding.config;

import com.br.mamba_wedding.guests.domain.Guest;
import com.br.mamba_wedding.guests.domain.GuestSide;
import com.br.mamba_wedding.guests.domain.GuestStatus;
import com.br.mamba_wedding.guests.infrastructure.GuestRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Profile("dsv")
public class GuestDataSeeder implements CommandLineRunner {

    private final GuestRepository guestRepository;

    @Override
    public void run(String... args) {
        if (guestRepository.count() != 0) {
            return;
        }

        Guest g1 = Guest.builder()
            .fullName("Fabiana Maia")
            .rsvpCode("FABI123")
            .rsvpStatus(GuestStatus.PENDING)
            .side(GuestSide.BRIDE)
            .email("fabiana.maia@gmail.com")
            .phone("21999999999")
            .build();

        Guest g2 = Guest.builder()
            .fullName("Cecile Azambuja")
            .rsvpCode("CECI123")
            .rsvpStatus(GuestStatus.PENDING)
            .side(GuestSide.GROOM)
            .email("cecile.azambuja@gmail.com")
            .phone("21999999999")
            .build();

        Guest g3 = Guest.builder()
            .fullName("Eliane Azambuja")
            .rsvpCode("ELIA123")
            .rsvpStatus(GuestStatus.PENDING)
            .side(GuestSide.GROOM)
            .email("eliane.azambuja@gmail.com")
            .phone("21999999999")
            .build();

        guestRepository.save(g1);
        guestRepository.save(g2);
        guestRepository.save(g3);

        System.out.println(">>> Convidados teste inseridos com sucesso");
    }
}