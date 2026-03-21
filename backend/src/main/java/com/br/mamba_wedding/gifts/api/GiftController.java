package com.br.mamba_wedding.gifts.api;

import com.br.mamba_wedding.gifts.api.dto.GiftDetail;
import com.br.mamba_wedding.gifts.api.dto.GiftList;
import com.br.mamba_wedding.gifts.application.GiftService;
import com.br.mamba_wedding.gifts.domain.Gift;
import com.br.mamba_wedding.guests.domain.Guest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/gifts")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class GiftController {

    private final GiftService giftService;

    @GetMapping
    public ResponseEntity<List<GiftList>> list() {
        List<GiftList> response = giftService.listAll()
                .stream()
                .map(GiftList::from)
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GiftDetail> detailGift(@PathVariable Long id){
        Gift response = giftService.buscarPorId(id);
        return ResponseEntity.ok(GiftDetail.from(response));
    }

    @PostMapping("/{id}/reserve")
    public ResponseEntity<Void> reserve(@PathVariable Long id, @AuthenticationPrincipal Guest loggedGuest) {
        giftService.reservar(id, loggedGuest.getNomeCompleto());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/reserve")
    public ResponseEntity<Void> cancelReserve(@PathVariable Long id, @AuthenticationPrincipal Guest loggedGuest){
        
        giftService.cancelarReserva(id, loggedGuest.getNomeCompleto());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/buy")
    public ResponseEntity<Void> buyGift(@PathVariable Long id, @AuthenticationPrincipal Guest loggedGuest){
        giftService.comprar(id, loggedGuest.getNomeCompleto());
        return ResponseEntity.noContent().build();
    }
}