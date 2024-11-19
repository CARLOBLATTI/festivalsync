package com.festivalsync.persistence.repositories;

import com.festivalsync.persistence.entities.SoldTickets;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SoldTicketsRepository extends JpaRepository<SoldTickets, Long> {
    // Ottenere tutti i biglietti venduti
    List<SoldTickets> findAll();

    // Ottenere un biglietto venduto per ID
    SoldTickets findById(long id);

    // Ottenere tutti i biglietti venduti per un evento
    @Query("SELECT st FROM SoldTicket st WHERE st.ticket.event.id = :eventId")
    List<SoldTickets> findSoldTicketsByEventId(long eventId);

    // Ottenere tutti i biglietti venduti di un certo tipo
    @Query("SELECT st FROM SoldTicket st WHERE st.ticket.id = :ticketId")
    List<SoldTickets> findSoldTicketsByTicketId(long ticketId);
}
