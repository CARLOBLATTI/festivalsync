package com.festivalsync.controllers;

import com.festivalsync.models.ArtistModel;
import com.festivalsync.models.kafkaMessage.ArtistEventMessage;
import com.festivalsync.models.request.AddArtistRequest;
import com.festivalsync.models.response.AddArtistResponse;
import com.festivalsync.models.response.GetArtistEventsResponse;
import com.festivalsync.persistence.entities.Artists;
import com.festivalsync.services.KafkaProducerService;
import com.festivalsync.services.ManageArtistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/artist-service")
@Tag(name = "Artist Controller", description = "API for managing artists")
public class ArtistController {

    @Value("${kafka.topic.artist}")
    String addArtistTopic;

    @Value("${kafka.topic.artist-deleted}")
    String deleteArtistTopic;

    @Autowired
    KafkaProducerService kafkaProducerService;

    @Autowired
    ManageArtistService manageArtistService;

    @Operation(summary = "Aggiungi un nuovo artista e invia un messaggio Kafka")
    @PostMapping("/add-artist")
    public ResponseEntity<AddArtistResponse> addArtist(@Valid @RequestBody AddArtistRequest request) {
        // Crea e salva l'artista
        Artists artist = new Artists();
        artist.setName(request.getName());
        artist.setGenre(request.getGenre());
        artist.setCountry(request.getCountry());
        artist.setLocation(request.getLocation());
        artist.setCreationDate(LocalDate.now());
        artist.setInsertTimestamp(LocalDateTime.now());
        artist.setUpdateTimestamp(LocalDateTime.now());
        artist = manageArtistService.saveAndFlushArtist(artist);

        // Crea il messaggio Kafka
        ArtistEventMessage kafkaMessage = new ArtistEventMessage();
        kafkaMessage.setArtistId(artist.getId());
        kafkaMessage.setDesiredEventCity(request.getDesiredEventCity());
        kafkaMessage.setDesiredEventDate(request.getDesiredEventDate().toString());

        // Invia il messaggio Kafka
        kafkaProducerService.sendMessage(addArtistTopic, kafkaMessage);

        // Prepara la risposta
        AddArtistResponse response = new AddArtistResponse();
        response.setId(artist.getId());
        response.setMessage("Artista aggiunto con successo");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/all-artist")
    @Operation(summary = "Recupera un tutti gli artisti registrati")
    public ResponseEntity<List<ArtistModel>> getArtistById() {
        List<Artists> artists = manageArtistService.findAllArtists();
        List<ArtistModel> artistsModel = artists.stream().map(manageArtistService::convertArtistToModel)
                .collect(Collectors.toList());
        return ResponseEntity.ok(artistsModel);
    }

    @Operation(summary = "Ottieni tutti gli eventi associati a un artista")
    @GetMapping("/get-artist-events/{name}")
    public ResponseEntity<GetArtistEventsResponse> getArtistEvents(@PathVariable String name) {
        // Recupera l'artista dal database
        List<Artists> artistRegistrations = manageArtistService.findArtistRegistrationsByName(name);
        if (artistRegistrations == null || artistRegistrations.isEmpty()) {
            throw new IllegalArgumentException("Registrazione dell'Artista con nome: " + name + "non trovate");
        };
        /*ArtistModel artistModel = manageArtistService.findArtistByName(name)
                .map(manageArtistService::convertArtistToModel)
                .orElseThrow(() -> new ResourceNotFoundException("Artista non trovato"));*/

        // Mappa gli eventi associati
        GetArtistEventsResponse response = new GetArtistEventsResponse();
        //response.setArtistModel(artistModel);
        for (Artists artistRegistration : artistRegistrations) {
            response.getEvents().add(manageArtistService.convertEventToModel(artistRegistration.getEvent()));
        }

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Elimina la partecipazione di un artista")
    @DeleteMapping("/delete-artist/{id}")
    public ResponseEntity<String> deleteArtist(@PathVariable Long id) {
        // Recupera l'artista
        Artists artist = manageArtistService.findArtistById(id)
                .orElseThrow(() -> new IllegalArgumentException("Artista non trovato con ID " + id));
        List<Long> eventsId = new ArrayList<>();
        /*List<Events> events = artist.getEvents();
        for (Events event : events) {
            eventsId.add(event.getId());
        }*/

        eventsId.add(artist.getEvent().getId());

        // Cancella l'artista dal database
        manageArtistService.deleteArtistById(id);

        // Crea il messaggio Kafka
        ArtistEventMessage kafkaMessage = new ArtistEventMessage();
        kafkaMessage.setArtistId(artist.getId());
        kafkaMessage.setEventsId(eventsId);
        kafkaProducerService.sendMessage(deleteArtistTopic, kafkaMessage);

        String responseMessage = "Partecipazione dell'Artista eliminata con successo";

        return ResponseEntity.ok(responseMessage);
    }

}
