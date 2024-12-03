package com.festivalsync.controllers;

import com.festivalsync.models.EventModel;
import com.festivalsync.models.kafkaMessage.ArtistEventMessage;
import com.festivalsync.models.kafkaMessage.EventMessage;
import com.festivalsync.persistence.entities.Artists;
import com.festivalsync.persistence.entities.Events;
import com.festivalsync.services.KafkaProducerService;
import com.festivalsync.services.ManageEventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/event-service")
@Tag(name = "Event Controller", description = "API for managing events")
public class EventController {

    @Autowired
    ManageEventService manageEventService;

    @Autowired
    KafkaProducerService kafkaProducerService;

    @Value("${kafka.topic.event-deleted}")
    private String eventDeletedTopic;

    @GetMapping("/{id}")
    @Operation(summary = "Recupera un evento tramite ID")
    public ResponseEntity<EventModel> getEventById(@PathVariable Long id) {
        EventModel event = manageEventService.findEventById(id)
                .map(manageEventService::convertEventToModel)
                .orElseThrow(() -> new ResourceNotFoundException("Evento non trovato"));
        return ResponseEntity.ok(event);
    }

    @GetMapping("/all")
    @Operation(summary = "Recupera tutti gli eventi creati")
    public ResponseEntity<List<EventModel>> getArtistById() {
        List<Events> events = manageEventService.findAllEvents();
        List<EventModel> eventsModels = events.stream().map(manageEventService::convertEventToModel)
                .collect(Collectors.toList());
        return ResponseEntity.ok(eventsModels);
    }

    @Operation(summary = "Elimina un evento")
    @DeleteMapping("/delete-event/{id}")
    public ResponseEntity<String> deleteArtist(@PathVariable Long id) {
        // Recupera l'artista
        Events event = manageEventService.findEventById(id)
                .orElseThrow(() -> new IllegalArgumentException("Evento non trovato con ID " + id));
        List<Long> artistsId = new ArrayList<>();
        List<Artists> artists = event.getArtists();
        for (Artists artist : artists) {
            artistsId.add(artist.getId());
        }

        // Cancella l'artista dal database
        manageEventService.deleteEvent(event);

        // Invia un messaggio al topic event-deleted
        EventMessage eventDeletedMessage = new EventMessage();
        eventDeletedMessage.setEventId(event.getId());
        kafkaProducerService.sendMessage(eventDeletedTopic, eventDeletedMessage);

        String responseMessage = "Evento cancellato con successo";

        return ResponseEntity.ok(responseMessage);
    }
}
