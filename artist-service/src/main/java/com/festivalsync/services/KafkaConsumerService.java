package com.festivalsync.services;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

    /*@KafkaListener(topics = "${kafka.topic.artist}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeMessage(String message) {
        System.out.println("Aggiunto un artista");
        // Gestione del messaggio (override nei micro-servizi per personalizzazione)
    }*/
}

