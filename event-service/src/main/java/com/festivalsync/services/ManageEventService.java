package com.festivalsync.services;

import com.festivalsync.models.ArtistModel;
import com.festivalsync.models.EventModel;
import com.festivalsync.persistence.entities.Artists;
import com.festivalsync.persistence.entities.Events;
import com.festivalsync.persistence.repositories.ArtistsRepository;
import com.festivalsync.persistence.repositories.EventsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ManageEventService {

    @Autowired
    private EventsRepository eventsRepository;

    @Autowired
    ArtistsRepository artistsRepository;

    @Transactional(rollbackFor = Exception.class)
    public Events saveAndFlushEvent(Events event) {
        return eventsRepository.saveAndFlush(event);
    }

    @Transactional(readOnly = true)
    public Optional<Events> findEventById(Long id) {
        return eventsRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Events findEventBylocationAndDate(String location, LocalDate eventDate) {
        return eventsRepository.findByLocationAndDate(location, eventDate);
    }

    @Transactional(readOnly = true)
    public List<Events> findAllEvents() {
        return eventsRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Events> findAllByArtistId(Long artistId) {
        return eventsRepository.findAllByArtistId(artistId);
    }

    @Transactional(rollbackFor = Exception.class)
    public Events updateEvent(Events event) {
        if (!eventsRepository.existsById(event.getId())) {
            throw new IllegalArgumentException("Event with ID " + event.getId() + " does not exist");
        }
        return eventsRepository.save(event);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteEventById(Long id) {
        if (!eventsRepository.existsById(id)) {
            throw new IllegalArgumentException("Event with ID " + id + " does not exist");
        }
        eventsRepository.deleteById(id);
    }

    /**
     * Recupera un artista tramite il suo ID.
     *
     * @param id L'ID dell'artista
     * @return Un Optional contenente l'artista, se trovato
     */
    @Transactional(readOnly = true)
    public Optional<Artists> findArtistById(Long id) {
        return artistsRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public long countEvents() {
        return eventsRepository.count();
    }

    public EventModel convertEventToModel(Events event) {
        EventModel model = new EventModel();
        model.setId(event.getId());
        model.setName(event.getName());
        model.setDate(event.getDate());
        model.setLocation(event.getLocation());
        model.setCountry(event.getCountry());
        model.setState(event.getState());
        model.setArtistsNumber(event.getArtistsNumber());
        model.setCreationDate(event.getCreationDate());

        // Converti la lista di artisti in ArtistModel
        if (event.getArtists() != null) {
            model.setArtists(event.getArtists().stream()
                    .map(this::convertArtistToModel)
                    .toList());
        }
        return model;
    }

    public Events convertEventToEntity(EventModel model) {
        Events event = new Events();
        event.setId(model.getId());
        event.setName(model.getName());
        event.setDate(model.getDate());
        event.setLocation(model.getLocation());
        event.setCountry(model.getCountry());
        event.setState(model.getState());
        event.setCreationDate(model.getCreationDate());
        event.setInsertTimestamp(model.getInsertTimestamp());
        event.setUpdateTimestamp(model.getUpdateTimestamp());

        // Converti la lista di artisti in Artists
        if (model.getArtists() != null) {
            event.setArtists(model.getArtists().stream()
                    .map(this::convertArtistModelToEntity)
                    .toList());
        }
        return event;
    }

    public ArtistModel convertArtistToModel(Artists artist) {
        ArtistModel model = new ArtistModel();
        model.setId(artist.getId());
        model.setName(artist.getName());
        model.setGenre(artist.getGenre());
        model.setCountry(artist.getCountry());
        model.setLocation(artist.getLocation());
        model.setState(artist.getState());
        model.setCreationDate(artist.getCreationDate());
        model.setInsertTimestamp(artist.getInsertTimestamp());
        model.setUpdateTimestamp(artist.getUpdateTimestamp());
        return model;
    }

    public Artists convertArtistModelToEntity(ArtistModel model) {
        Artists artist = new Artists();
        artist.setId(model.getId());
        artist.setName(model.getName());
        artist.setGenre(model.getGenre());
        artist.setCountry(model.getCountry());
        artist.setLocation(model.getLocation());
        artist.setState(model.getState());
        artist.setCreationDate(model.getCreationDate());
        artist.setInsertTimestamp(model.getInsertTimestamp());
        artist.setUpdateTimestamp(model.getUpdateTimestamp());
        return artist;
    }

}
