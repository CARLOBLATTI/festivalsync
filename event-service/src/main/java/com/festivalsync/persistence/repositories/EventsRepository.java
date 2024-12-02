package com.festivalsync.persistence.repositories;

import com.festivalsync.persistence.entities.Events;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EventsRepository extends JpaRepository<Events, Long> {
    // Ottenere tutti gli eventi
    List<Events> findAll();

    // Ottenere un evento per ID
    Events findById(long id);

    @Query("SELECT e FROM Events e WHERE e.location = :location AND e.date = :date")
    Events findByLocationAndDate(@Param("location") String location, @Param("date") LocalDate date);

    @Query("SELECT e FROM Events e JOIN e.artists a WHERE a.id = :artistId")
    List<Events> findAllByArtistId(@Param("artistId") Long artistId);


}

