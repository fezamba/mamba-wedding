package com.br.mamba_wedding.gifts.api;

import com.br.mamba_wedding.gifts.api.dto.GiftCreate;
import com.br.mamba_wedding.gifts.api.dto.GiftCreated;
import com.br.mamba_wedding.gifts.api.dto.GiftDetail;
import com.br.mamba_wedding.gifts.api.dto.GiftList;
import com.br.mamba_wedding.gifts.api.dto.ReserveRequest;
import com.br.mamba_wedding.gifts.application.GiftService;
import com.br.mamba_wedding.gifts.domain.Gift;
import com.br.mamba_wedding.guests.domain.Guest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
        Gift response = giftService.findById(id);
        return ResponseEntity.ok(GiftDetail.from(response));
    }

    @PostMapping("/{id}/reserve")
    public ResponseEntity<Void> reserve(@PathVariable Long id, @AuthenticationPrincipal Guest loggedGuest, @Valid @RequestBody ReserveRequest request) {
        giftService.reserve(id, loggedGuest.getFullName(), request.quotas());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/reserve")
    public ResponseEntity<Void> cancelReserve(@PathVariable Long id, @AuthenticationPrincipal Guest loggedGuest){
        giftService.cancelReserve(id, loggedGuest.getFullName());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/buy")
    public ResponseEntity<Void> buyGift(@PathVariable Long id, @AuthenticationPrincipal Guest loggedGuest){
        giftService.buy(id, loggedGuest.getFullName());
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/register")
    public ResponseEntity<GiftCreated> registerGift(@Valid @RequestBody GiftCreate gift){
        GiftCreated response = giftService.register(gift);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}/delete")
    public ResponseEntity<Void> deleteGift(@PathVariable Long id){
        giftService.delete(id);
        return ResponseEntity.noContent().build();
    }
}