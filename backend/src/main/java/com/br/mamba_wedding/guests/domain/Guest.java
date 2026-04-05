package com.br.mamba_wedding.guests.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "guests", indexes = {
        @Index(name = "idx_guests_rsvp_code", columnList = "rsvpCode", unique = true)
})
public class Guest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true, length = 32)
    private String rsvpCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private GuestStatus rsvpStatus;

    private LocalDateTime rsvpBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private GuestSide side;

    @Column(nullable = false, length = 120)
    private String email;

    @Column(nullable = false, length = 30)
    private String phone;

    @Column(length = 255)
    private String notes;
}