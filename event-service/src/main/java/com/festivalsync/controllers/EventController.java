package com.festivalsync.controllers;

import com.festivalsync.models.EventModel;
import com.festivalsync.persistence.entities.Events;
import com.festivalsync.services.ManageEventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/event-service")
@Tag(name = "Event Controller", description = "API for managing events")
public class EventController {

    @Autowired
    ManageEventService manageEventService;

    @GetMapping("/{id}")
    @Operation(summary = "Recupera un evento tramite ID")
    public ResponseEntity<EventModel> getEventById(@PathVariable Long id) {
        EventModel event = manageEventService.findEventById(id)
                .map(manageEventService::convertEventToModel)
                .orElseThrow(() -> new ResourceNotFoundException("Evento non trovato"));
        return ResponseEntity.ok(event);
    }

    @GetMapping("/all-artist")
    @Operation(summary = "Recupera un tutti gli eventi creati")
    public ResponseEntity<List<EventModel>> getArtistById() {
        List<Events> events = manageEventService.findAllEvents();
        List<EventModel> eventsModels = events.stream().map(manageEventService::convertEventToModel)
                .collect(Collectors.toList());
        return ResponseEntity.ok(eventsModels);
    }
}
