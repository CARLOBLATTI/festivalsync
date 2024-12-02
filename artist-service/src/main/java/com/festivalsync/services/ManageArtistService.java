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
     * Recupera tutti gli artisti nel database.
     *
     * @return Una lista di tutti gli artisti
     */
    @Transactional(readOnly = true)
    public List<Artists> findAllArtists() {
        return artistsRepository.findAll();
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

        // Converti la lista di artisti in ArtistModel
        if (event.getArtists() != null) {
            model.setArtists(event.getArtists().stream()
                    .map(this::convertArtistToModel)
                    .toList());
        }
        return model;
    }

    /*public Events convertEventToEntity(EventModel model) {
        Events event = new Events();
        event.setId(model.getId());
        event.setName(model.getName());
        event.setDate(model.getDate());
        event.setLocation(model.getLocation());
        event.setCountry(model.getCountry());
        event.setState(model.getState());
        event.setCreationDate(model.getCreationDate());

        // Converti la lista di artisti in Artists
        if (model.getArtists() != null) {
            event.setArtists(model.getArtists().stream()
                    .map(this::convertArtistModelToEntity)
                    .toList());
        }
        return event;
    }*/

}
