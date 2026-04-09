package com.br.mamba_wedding.guests.api.dto;

import com.br.mamba_wedding.guests.domain.Guest;
import com.br.mamba_wedding.guests.domain.GuestSide;

public record GuestCreated (

        String fullName,

        String rsvpCode,

        GuestSide side,

        String email,

        String phone

) {
    public GuestCreated (Guest guest){
        this(guest.getFullName(), guest.getRsvpCode(), guest.getSide(), guest.getEmail(), guest.getPhone());
    }
}