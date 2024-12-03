package com.festivalsync.services;

import com.festivalsync.models.kafkaMessage.ArtistEventMessage;
import com.festivalsync.models.kafkaMessage.EventMessage;
import com.festivalsync.persistence.entities.Artists;
import com.festivalsync.persistence.entities.Events;
import com.festivalsync.persistence.repositories.ArtistsRepository;
import com.festivalsync.persistence.repositories.EventsRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class KafkaConsumerService {

    @Autowired
    private EventsRepository eventsRepository;

    @Autowired
    private ArtistsRepository artistsRepository;

    @Autowired
    ManageEventService manageEventService;

    @Autowired
    KafkaProducerService kafkaProducerService;

    @Value("${kafka.topic.event}")
    String eventCreatedTopic;

    @Value("${kafka.topic.event-deleted}")
    private String eventDeletedTopic;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = "${kafka.topic.artist}", groupId = "${spring.kafka.consumer.group-id}")
    @Transactional(rollbackFor = Exception.class)
    public void consumeAddArtistMessage(String message) {
        try {
            // Parsing del messaggio JSON
            ArtistEventMessage artistMessage = objectMapper.readValue(message, ArtistEventMessage.class);
            System.out.println("Messaggio ricevuto da Kafka: " + artistMessage);

            // Recupera l'artista dal database
            Artists artist = manageEventService.findArtistById(artistMessage.getArtistId())
                    .orElseThrow(() -> new IllegalArgumentException("Artista non trovato con ID " + artistMessage.getArtistId()));

            // Cerca un evento esistente nella stessa città e data
            Events existingEvent = manageEventService.findEventByLocationAndDate(
                    artistMessage.getDesiredEventCity(),
                    LocalDate.parse(artistMessage.getDesiredEventDate())
            );

            if (existingEvent != null) {
                // Aggiunge l'artista all'evento esistente
                if (!existingEvent.getArtists().contains(artist)) {
                    existingEvent.getArtists().add(artist);
                    existingEvent.setArtistsNumber(existingEvent.getArtistsNumber() + 1);
                    existingEvent.setUpdateTimestamp(LocalDateTime.now());
                    eventsRepository.save(existingEvent);
                    artist.setEvent(existingEvent);
                    manageEventService.saveAndFlushArtists(artist);
                    System.out.println("Artista aggiunto all'evento esistente con ID " + existingEvent.getId());
                } else {
                    System.out.println("L'artista è già associato all'evento con ID " + existingEvent.getId());
                }
            } else {
                // Crea un nuovo evento
                Events newEvent = new Events();
                newEvent.setName("Evento per " + artist.getName());
                newEvent.setDate(LocalDate.parse(artistMessage.getDesiredEventDate()));
                newEvent.setLocation(artistMessage.getDesiredEventCity());
                newEvent.setCountry(artist.getCountry());
                newEvent.setState("scheduled");
                newEvent.setArtistsNumber(1);
                newEvent.setCreationDate(LocalDate.now());
                newEvent.setInsertTimestamp(LocalDateTime.now());
                newEvent.setUpdateTimestamp(LocalDateTime.now());
                newEvent.getArtists().add(artist);

                // Salva il nuovo evento
                newEvent = eventsRepository.save(newEvent);
                artist.setEvent(newEvent);
                manageEventService.saveAndFlushArtists(artist);
                System.out.println("Creato nuovo evento con ID " + newEvent.getId() + " e associato all'artista " + artist.getName());

                // Invia il messaggio al topic event-added
                EventMessage eventAddedMessage = new EventMessage();
                eventAddedMessage.setEventId(newEvent.getId());

                kafkaProducerService.sendMessage(eventCreatedTopic, eventAddedMessage);
                System.out.println("Messaggio inviato al topic " + eventCreatedTopic + ": " + eventAddedMessage);
            }

        } catch (Exception e) {
            System.err.println("Errore durante l'elaborazione del messaggio Kafka: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @KafkaListener(topics = "${kafka.topic.artist-deleted}", groupId = "${spring.kafka.consumer.group-id}")
    @Transactional(rollbackFor = Exception.class)
    public void consumeDeleteArtistMessage(String message) {
        try {
            // Parsing del messaggio JSON
            ArtistEventMessage artistDeletedMessage = objectMapper.readValue(message, ArtistEventMessage.class);
            System.out.println("Messaggio ricevuto da Kafka per delete-artist: " + artistDeletedMessage);

            Long artistId = artistDeletedMessage.getArtistId();

            for (Long eventId : artistDeletedMessage.getEventsId()) {
                // Rimuovi l'artista dalla relazione
                Events event = manageEventService.getEventById(eventId);
                event.getArtists().removeIf(artist -> artist.getId().equals(artistId));
                event.setArtistsNumber(event.getArtistsNumber() - 1);

                // Se l'evento non ha più artisti, eliminare l'evento
                if (event.getArtistsNumber() < 1) {
                    manageEventService.deleteEvent(event);

                    // Invia un messaggio al topic event-deleted
                    EventMessage eventDeletedMessage = new EventMessage();
                    eventDeletedMessage.setEventId(event.getId());
                    kafkaProducerService.sendMessage(eventDeletedTopic, eventDeletedMessage);
                    System.out.println("Evento eliminato con ID " + event.getId() + " e messaggio inviato al topic " + eventDeletedTopic);
                } else {
                    event.setUpdateTimestamp(LocalDateTime.now());
                    // Salva l'evento aggiornato
                    manageEventService.saveAndFlushEvent(event);
                }
            }

            System.out.println("Relazioni rimosse per l'artista con ID " + artistId);

        } catch (Exception e) {
            System.err.println("Errore durante l'elaborazione del messaggio delete-artist: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
