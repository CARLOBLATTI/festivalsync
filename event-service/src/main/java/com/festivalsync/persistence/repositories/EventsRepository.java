package com.festivalsync.persistence.repositories;

import com.festivalsync.persistence.entities.Events;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventsRepository extends JpaRepository<Events, Long> {
    // Ottenere tutti gli eventi
    List<Events> findAll();

    // Ottenere un evento per ID
    Events findById(long id);

}

