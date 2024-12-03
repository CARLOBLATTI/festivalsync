package com.festivalsync.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.festivalsync.models.kafkaMessage.EventMessage;
import com.festivalsync.persistence.entities.Artists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class KafkaConsumerService {

    @Autowired
    ManageArtistService manageArtistService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = "${kafka.topic.event-deleted}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeEventDeletedMessage(String message) {
        try {
            // Parsing del messaggio JSON
            EventMessage eventDeletedMessage = objectMapper.readValue(message, EventMessage.class);
            System.out.println("Messaggio ricevuto per la cancellazione dell'evento: " + eventDeletedMessage);

            Long eventId = eventDeletedMessage.getEventId();

            // Recupera tutti gli artisti associati all'evento
            List<Artists> artists = manageArtistService.findArtistsByEventId(eventId);

            if (artists != null && !artists.isEmpty()) {
                // Aggiorna i biglietti venduti associati a questo ticket
                for (Artists artist : artists) {
                    manageArtistService.deleteArtist(artist);
                }
                System.out.println("Eliminate le registrazione degli artisti all'evento con ID: " + eventId);
            }
            else {
                System.out.println("nessuna registrazione di artisti da cancellare all'evento con ID: " + eventId);

            }

        } catch (Exception e) {
            System.err.println("Errore durante l'elaborazione del messaggio event-deleted: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

