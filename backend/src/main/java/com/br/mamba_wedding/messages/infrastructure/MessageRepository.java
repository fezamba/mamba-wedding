package com.br.mamba_wedding.messages.infrastructure;

import com.br.mamba_wedding.messages.domain.Message;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface MessageRepository extends MongoRepository<Message, String> {
    List<Message> findAllByOrderBySendDateDesc();
}