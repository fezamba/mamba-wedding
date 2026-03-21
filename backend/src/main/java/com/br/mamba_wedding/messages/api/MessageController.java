package com.br.mamba_wedding.messages.api;

import com.br.mamba_wedding.guests.domain.Guest;
import com.br.mamba_wedding.messages.application.MessageService;
import com.br.mamba_wedding.messages.domain.Message;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@CrossOrigin(origins = "*")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    public record MessageRequest(String texto) {}

    @GetMapping
    public ResponseEntity<List<Message>> listar() {
        return ResponseEntity.ok(messageService.listarRecados());
    }

    @PostMapping
    public ResponseEntity<Message> criar(
            @RequestBody MessageRequest request,
            @AuthenticationPrincipal Guest guestLogado 
    ) {
        Message mensagemSalva = messageService.deixarRecado(guestLogado.getNomeCompleto(), request.texto());
        return ResponseEntity.ok(mensagemSalva);
    }
}