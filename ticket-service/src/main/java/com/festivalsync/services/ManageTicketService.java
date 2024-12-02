package com.festivalsync.services;

import com.festivalsync.persistence.entities.Tickets;
import com.festivalsync.persistence.repositories.TicketsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ManageTicketService {

    @Autowired
    private TicketsRepository ticketsRepository;

    @Transactional(rollbackFor = Exception.class)
    public Tickets saveAndFlushTicket(Tickets ticket) {
        return ticketsRepository.saveAndFlush(ticket);
    }

    @Transactional(readOnly = true)
    public Optional<Tickets> findTicketById(Long id) {
        return ticketsRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Tickets findTicketByEventId(Long id) {
        return ticketsRepository.findByEventId(id);
    }

    @Transactional(readOnly = true)
    public List<Tickets> findAllTickets() {
        return ticketsRepository.findAll();
    }

    @Transactional(rollbackFor = Exception.class)
    public Tickets updateTicket(Tickets ticket) {
        if (!ticketsRepository.existsById(ticket.getId())) {
            throw new IllegalArgumentException("Ticket with ID " + ticket.getId() + " does not exist");
        }
        return ticketsRepository.save(ticket);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteTicketById(Long id) {
        if (!ticketsRepository.existsById(id)) {
            throw new IllegalArgumentException("Ticket with ID " + id + " does not exist");
        }
        ticketsRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public long countTickets() {
        return ticketsRepository.count();
    }
}
