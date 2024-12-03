package com.festivalsync.models.kafkaMessage;

import lombok.Data;

@Data
public class EventMessage {
    private Long eventId;

    // Costruttore vuoto necessario per la deserializzazione
    public EventMessage() {
    }
}

