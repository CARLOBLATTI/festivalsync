package com.festivalsync.persistence.repositories;

import com.festivalsync.persistence.entities.Tickets;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketsRepository extends JpaRepository<Tickets, Long> {
    // Ottenere tutti i biglietti
    List<Tickets> findAll();

    // Ottenere un biglietto per ID
    Tickets findById(long id);

    // Ottenere tutti i biglietti di un evento
    @Query("SELECT t FROM Ticket t WHERE t.event.id = :eventId")
    List<Tickets> findTicketsByEventId(long eventId);
}
