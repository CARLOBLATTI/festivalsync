package com.festivalsync.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

    @Autowired
    KafkaProducerService kafkaProducerService;

    @Value("${kafka.topic.event}")
    String eventTopicName;

    @KafkaListener(topics = "${kafka.topic.artist}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeMessage(String message) {
        System.out.println("Received message: " + message);
        System.out.println("Aggiungo l'evento nel topic: " + eventTopicName);
        kafkaProducerService.sendMessage(eventTopicName, "new artist");
    }
}

