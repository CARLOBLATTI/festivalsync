package com.festivalsync.services;

import com.festivalsync.models.ArtistModel;
import com.festivalsync.models.EventModel;
import com.festivalsync.persistence.entities.Artists;
import com.festivalsync.persistence.entities.Events;
import com.festivalsync.persistence.repositories.ArtistsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ManageArtistService {

    @Autowired
    private ArtistsRepository artistsRepository;

    /**
     * Salva un nuovo artista nel database e flush immediato.
     * Il metodo è transazionale per garantire il rollback in caso di errori.
     *
     * @param artist L'entità Artist da salvare
     * @return L'entità Artist salvata
     */
    @Transactional(rollbackFor = Exception.class)
    public Artists saveAndFlushArtist(Artists artist) {
        return artistsRepository.saveAndFlush(artist);
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

    /**
     * Recupera un artista tramite il suo ID.
     *
     * @param name Il nome dell'artista
     * @return Un Optional contenente l'artista, se trovato
     */
    @Transactional(readOnly = true)
    public List<Artists> findArtistRegistrationsByName(String name) {
        return artistsRepository.findByName(name);
    }

    /**
     * Recupera tutti gli artisti nel database.
     *
     * @return Una lista di tutti gli artisti
     */
    @Transactional(readOnly = true)
    public List<Artists> findAllArtists() {
        return artistsRepository.findAll();
    }

    /**
     * Recupera tutti gli artisti nel database associati ad un evento.
     *
     * @return Una lista di tutti gli artisti associati ad un evento
     */
    @Transactional(readOnly = true)
    public List<Artists> findArtistsByEventId(Long eventId) {
        return artistsRepository.findArtistsByEventId(eventId);
    }

    /**
     * Aggiorna un artista esistente.
     *
     * @param artist L'entità Artist aggiornata
     * @return L'entità Artist salvata
     */
    @Transactional(rollbackFor = Exception.class)
    public Artists updateArtist(Artists artist) {
        if (!artistsRepository.existsById(artist.getId())) {
            throw new IllegalArgumentException("Artist with ID " + artist.getId() + " does not exist");
        }
        return artistsRepository.save(artist);
    }

    /**
     * Elimina un artista tramite il suo ID.
     *
     * @param id L'ID dell'artista da eliminare
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteArtistById(Long id) {
        if (!artistsRepository.existsById(id)) {
            throw new IllegalArgumentException("Artist with ID " + id + " does not exist");
        }
        artistsRepository.deleteById(id);
    }

    /**
     * Elimina la registrazione di un'artista.
     *
     * @param artist La registrazione dell'artista da eliminare
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteArtist(Artists artist) {
        artistsRepository.delete(artist);
    }

    /**
     * Conta il numero totale di artisti nel database.
     *
     * @return Il numero totale di artisti
     */
    @Transactional(readOnly = true)
    public long countArtists() {
        return artistsRepository.count();
    }


    public ArtistModel convertArtistToModel(Artists artist) {
        ArtistModel model = new ArtistModel();
        model.setId(artist.getId());
        model.setName(artist.getName());
        model.setGenre(artist.getGenre());
        if (artist.getEvent() != null) {
            model.setEvents(convertEventToModel(artist.getEvent()));
        }
        /*if (artist.getEvents() != null) {
            model.setEvents(artist.getEvents().stream()
                    .map(this::convertEventToModel)
                    .toList());
        }*/
        model.setCountry(artist.getCountry());
        model.setLocation(artist.getLocation());
        model.setState(artist.getState());
        model.setCreationDate(artist.getCreationDate());
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
        /*if (model.getEvents() != null) {
            artist.setEvents(model.getEvents().stream()
                    .map(this::convertEventToEntity)
                    .toList());
        }*/
        if (model.getEvents() != null) {
            artist.setEvent(convertEventToEntity(model.getEvents()));
        }
        artist.setCreationDate(model.getCreationDate());
        return artist;
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

        return event;
    }

}
