package com.br.mamba_wedding.messages.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "messages")
public class Message {

    @Id
    private String id;
    
    private String author;
    private String text;
    private LocalDateTime sendDate;

    public Message() {}

    public Message(String author, String text) {
        this.author = author;
        this.text = text;
        this.sendDate = LocalDateTime.now();
    }

    public String getId() { return id; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    public LocalDateTime getSendDate() { return sendDate; }
    public void setSendDate(LocalDateTime sendDate) { this.sendDate = sendDate; }
}