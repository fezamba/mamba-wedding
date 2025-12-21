package com.br.mamba_wedding.guests.domain;

import com.br.mamba_wedding.common.exception.NotFoundException;

public class GuestNotFoundException extends NotFoundException {
    public GuestNotFoundException() {
        super("Código de convidado não encontrado.");
    }
    
}
