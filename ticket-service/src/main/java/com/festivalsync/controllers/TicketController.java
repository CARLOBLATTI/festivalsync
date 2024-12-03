package com.festivalsync.controllers;

import com.festivalsync.models.PurchaseTicketRequest;
import com.festivalsync.models.RefundTicketRequest;
import com.festivalsync.persistence.entities.SoldTickets;
import com.festivalsync.persistence.entities.Tickets;
import com.festivalsync.persistence.repositories.SoldTicketsRepository;
import com.festivalsync.persistence.repositories.TicketsRepository;
import com.festivalsync.services.ManageSoldTicketService;
import com.festivalsync.services.ManageTicketService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/ticket-service")
public class TicketController {

    @Autowired
    ManageTicketService manageTicketService;

    @Autowired
    ManageSoldTicketService manageSoldTicketService;

    @Operation(summary = "Ottieni i dettagli di un ticket in base all'eventId")
    @GetMapping("/tickets/{eventId}")
    public ResponseEntity<Tickets> getTicketsByEventId(@PathVariable Long eventId) {
        Tickets ticket = manageTicketService.findTicketByEventId(eventId);
        return ResponseEntity.ok(ticket);
    }

    @Operation(summary = "Compra uno o più ticket")
    @PostMapping("/purchase")
    public ResponseEntity<String> purchaseTicket(@RequestBody PurchaseTicketRequest request) {
        Tickets ticket = manageTicketService.findTicketById(request.getTicketId())
                .orElseThrow(() -> new IllegalArgumentException("Ticket non trovato con ID " + request.getTicketId()));

        if (!"available".equalsIgnoreCase(ticket.getState()) || ticket.getAvailability() < request.getQuantity()) {
            throw new IllegalStateException("Ticket non disponibile o quantità insufficiente.");
        }

        // Crea SoldTicket in base alla quantità, l'id è il codice univoco del ticket
        for (int i = 0; i < request.getQuantity(); i++) {
            SoldTickets soldTicket = new SoldTickets();
            soldTicket.setTicket(ticket);
            soldTicket.setBuyerName(request.getBuyerName());
            soldTicket.setBuyerEmail(request.getBuyerEmail());
            soldTicket.setBuyerPhone(request.getBuyerPhone());
            soldTicket.setState("sold");
            soldTicket.setPurchaseDate(LocalDateTime.now());
            soldTicket.setCreationDate(LocalDate.now());
            soldTicket.setInsertTimestamp(LocalDateTime.now());
            soldTicket.setUpdateTimestamp(LocalDateTime.now());
            manageSoldTicketService.saveAndFlushSoldTicket(soldTicket);
        }

        // Aggiorna disponibilità del ticket
        ticket.setAvailability(ticket.getAvailability() - request.getQuantity());
        if (ticket.getAvailability() < 1) {
            ticket.setState("NOT_AVAILABLE");
        }
        ticket.setUpdateTimestamp(LocalDateTime.now());
        manageTicketService.saveAndFlushTicket(ticket);


        return ResponseEntity.ok("Ticket acquistato con successo!");
    }

    @Operation(summary = "Richiedi il rimborso di un ticket")
    @PutMapping("/refund")
    public ResponseEntity<String> refundTicket(@RequestBody RefundTicketRequest request) {
        SoldTickets soldTicket = manageSoldTicketService.findSoldTicketById(request.getSoldTicketId())
                .orElseThrow(() -> new IllegalArgumentException("SoldTicket non trovato con ID " + request.getSoldTicketId()));

        if (!"sold".equalsIgnoreCase(soldTicket.getState())) {
            throw new IllegalStateException("Il ticket non può essere rimborsato.");
        }

        // Aggiorna stato di SoldTicket
        soldTicket.setState("refunded");
        soldTicket.setUpdateTimestamp(LocalDateTime.now());
        manageSoldTicketService.saveAndFlushSoldTicket(soldTicket);

        // Aggiorna disponibilità del ticket
        Tickets ticket = soldTicket.getTicket();
        ticket.setAvailability(ticket.getAvailability() + 1);
        if ("not_available".equalsIgnoreCase(ticket.getState()) && ticket.getAvailability() < 1) {
            ticket.setState("AVAILABLE");
        }
        ticket.setUpdateTimestamp(LocalDateTime.now());
        manageTicketService.saveAndFlushTicket(ticket);
        return ResponseEntity.ok("Rimborso effettuato con successo!");
    }
}
