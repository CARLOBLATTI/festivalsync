package com.festivalsync.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.festivalsync.models.kafkaMessage.EventMessage;
import com.festivalsync.persistence.entities.SoldTickets;
import com.festivalsync.persistence.entities.Tickets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;


@Service
public class KafkaConsumerService {

    @Autowired
    KafkaProducerService kafkaProducerService;

    @Autowired
    ManageTicketService manageTicketService;

    @Autowired
    ManageSoldTicketService manageSoldTicketService;

    @Value("${kafka.topic.event-deleted}")
    private String eventDeletedTopic;


    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Random random = new Random();


    @KafkaListener(topics = "${kafka.topic.event}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeEventAddedMessage(String message) {
        try {
            // Parsing del messaggio JSON
            EventMessage eventMessage = objectMapper.readValue(message, EventMessage.class);
            System.out.println("Messaggio ricevuto per la creazione di un evento: " + eventMessage);

            // Creazione di un nuovo ticket
            Tickets ticket = new Tickets();
            ticket.setEventId(eventMessage.getEventId());
            ticket.setPrice(getRandomPrice()); // Prezzo randomico tra 20 e 60
            ticket.setAvailability(getRandomAvailablePlaces()); // Posti disponibili tra 50 e 100
            ticket.setState("AVAILABLE");
            ticket.setCreationDate(LocalDate.now());
            ticket.setInsertTimestamp(LocalDateTime.now());
            ticket.setUpdateTimestamp(LocalDateTime.now());

            // Salvataggio del ticket
            manageTicketService.saveAndFlushTicket(ticket);
            System.out.println("Creato un nuovo ticket per l'evento ID " + eventMessage.getEventId());

        } catch (Exception e) {
            System.err.println("Errore durante l'elaborazione del messaggio Kafka: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @KafkaListener(topics = "${kafka.topic.event-deleted}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeEventDeletedMessage(String message) {
        try {
            // Parsing del messaggio JSON
            EventMessage eventDeletedMessage = objectMapper.readValue(message, EventMessage.class);
            System.out.println("Messaggio ricevuto per la cancellazione dell'evento: " + eventDeletedMessage);

            Long eventId = eventDeletedMessage.getEventId();

            // Recupera tutti i ticket associati all'evento
            Tickets ticket = manageTicketService.findTicketByEventId(eventId);
            List<SoldTickets> soldTickets = manageSoldTicketService.findSoldTicketByTicketId(ticket.getId());

            int i = 0;
            // Aggiorna i biglietti venduti associati a questo ticket
            for (SoldTickets soldTicket : soldTickets) {
                soldTicket.setState("REFUNDED");
                soldTicket.setUpdateTimestamp(LocalDateTime.now());
                manageSoldTicketService.saveAndFlushSoldTicket(soldTicket);
                i++;
            }

            // Ripristina l'availability e aggiorna lo stato del ticket
            ticket.setAvailability(ticket.getAvailability() + i);
            ticket.setState("DELETED");
            ticket.setUpdateTimestamp(LocalDateTime.now());
            manageTicketService.saveAndFlushTicket(ticket);

            System.out.println("Aggiornato lo stato del ticket con ID " + ticket.getId() + " a 'deleted'");

        } catch (Exception e) {
            System.err.println("Errore durante l'elaborazione del messaggio event-deleted: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private BigDecimal getRandomPrice() {
        return BigDecimal.valueOf(20.0 + (random.nextDouble() * (60.0 - 20.0)));
    }

    private int getRandomAvailablePlaces() {
        return 50 + random.nextInt(51);
    }
}

