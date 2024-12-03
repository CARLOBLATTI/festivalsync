package com.festivalsync.models.kafkaMessage;

import lombok.Data;

import java.util.List;

@Data
public class ArtistEventMessage {
    private Long artistId;
    private String desiredEventCity;
    private String desiredEventDate;
    private List<Long> eventsId;

    // Costruttore vuoto necessario per la deserializzazione
    public ArtistEventMessage() {
    }
}

