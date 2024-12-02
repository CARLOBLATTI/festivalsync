package com.festivalsync.services;

import com.festivalsync.persistence.entities.SoldTickets;
import com.festivalsync.persistence.repositories.SoldTicketsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ManageSoldTicketService {

    @Autowired
    private SoldTicketsRepository soldTicketsRepository;

    @Transactional(rollbackFor = Exception.class)
    public SoldTickets saveAndFlushSoldTicket(SoldTickets soldTicket) {
        return soldTicketsRepository.saveAndFlush(soldTicket);
    }

    @Transactional(readOnly = true)
    public Optional<SoldTickets> findSoldTicketById(Long id) {
        return soldTicketsRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<SoldTickets> findAllSoldTickets() {
        return soldTicketsRepository.findAll();
    }

    @Transactional(rollbackFor = Exception.class)
    public SoldTickets updateSoldTicket(SoldTickets soldTicket) {
        if (!soldTicketsRepository.existsById(soldTicket.getId())) {
            throw new IllegalArgumentException("Ticket with ID " + soldTicket.getId() + " does not exist");
        }
        return soldTicketsRepository.save(soldTicket);
    }
    @Transactional(rollbackFor = Exception.class)
    public void deleteSoldTicketById(Long id) {
        if (!soldTicketsRepository.existsById(id)) {
            throw new IllegalArgumentException("Ticket with ID " + id + " does not exist");
        }
        soldTicketsRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public long countSoldTickets() {
        return soldTicketsRepository.count();
    }

    @Transactional(readOnly = true)
    public List<SoldTickets> findSoldTicketByTicketId(Long ticketId) {
        return soldTicketsRepository.findSoldTicketsByTicketId(ticketId);
    }
}
