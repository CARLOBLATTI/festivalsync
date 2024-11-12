package com.festivalsync.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

    @Autowired
    KafkaProducerService kafkaProducerService;

    @KafkaListener(topics = "${kafka.topic.event}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeMessage(String message) {
        System.out.println("Received message: " + message + " adesso vendiamo i biglietti");
        //kafkaProducerService.sendMessage("${kafka.topic.event}", "new artist");
    }
}

