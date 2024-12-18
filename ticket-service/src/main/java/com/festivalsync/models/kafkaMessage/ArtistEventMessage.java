package com.festivalsync.models.kafkaMessage;

import lombok.Data;

@Data
public class ArtistEventMessage {
    private Long artistId;
    private String desiredEventCity;
    private String desiredEventDate;

    // Costruttore vuoto necessario per la deserializzazione
    public ArtistEventMessage() {
    }
}

