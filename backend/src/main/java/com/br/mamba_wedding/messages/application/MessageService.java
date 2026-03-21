package com.br.mamba_wedding.messages.application;

import com.br.mamba_wedding.messages.domain.Message;
import com.br.mamba_wedding.messages.infrastructure.MessageRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {

    private final MessageRepository messageRepository;

    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public Message deixarRecado(String autor, String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            throw new IllegalArgumentException("A mensagem não pode estar vazia.");
        }
        Message novaMensagem = new Message(autor, texto);
        return messageRepository.save(novaMensagem);
    }

    public List<Message> listarRecados() {
        return messageRepository.findAllByOrderByDataEnvioDesc();
    }
}