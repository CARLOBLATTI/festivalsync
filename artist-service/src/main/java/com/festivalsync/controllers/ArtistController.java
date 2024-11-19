package com.festivalsync.controllers;

import com.festivalsync.services.KafkaProducerService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/artist-service")
public class ArtistController {

    @Value("${kafka.topic.artist}")
    String addArtistTopic;

    @Autowired
    KafkaProducerService kafkaProducerService;

    @Operation(summary = "An artist adds his presence")
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
