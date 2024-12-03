package com.festivalsync.persistence.repositories;

import com.festivalsync.persistence.entities.Artists;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArtistsRepository extends JpaRepository<Artists, Long> {
    // Ottenere tutti gli artisti
    List<Artists> findAll();

    // Ottenere un artista per ID
    Artists findById(long id);

    // Ottenere un artista per ID
    List<Artists> findByName(String name);

    // Ottenere tutti gli artisti associati a un evento
    @Query("SELECT a FROM Artists a WHERE a.event.id = :eventId")
    List<Artists> findArtistsByEventId(@Param("eventId") Long eventId);
}

