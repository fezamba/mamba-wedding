package com.br.mamba_wedding.messages.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "messages")
public class Message {

    @Id
    private String id;
    
    private String autor;
    private String texto;
    private LocalDateTime dataEnvio;

    public Message() {}

    public Message(String autor, String texto) {
        this.autor = autor;
        this.texto = texto;
        this.dataEnvio = LocalDateTime.now();
    }

    public String getId() { return id; }
    public String getAutor() { return autor; }
    public void setAutor(String autor) { this.autor = autor; }
    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }
    public LocalDateTime getDataEnvio() { return dataEnvio; }
    public void setDataEnvio(LocalDateTime dataEnvio) { this.dataEnvio = dataEnvio; }
}