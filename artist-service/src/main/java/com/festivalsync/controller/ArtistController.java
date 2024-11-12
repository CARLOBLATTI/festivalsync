package com.festivalsync.controller;

import com.festivalsync.services.KafkaProducerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ArtistController {

    @Value("${kafka.topic.artist}")
    String addArtistTopic;

    @Autowired
    KafkaProducerService kafkaProducerService;

    @PostMapping("/add-artist")
    public String addArtist(){
        String response = "ok";
        try {
            String test = "artist added";
            kafkaProducerService.sendMessage(addArtistTopic, test);
        }
        catch(Exception e){
            response = "error";
        }
        return response;
    }
}
