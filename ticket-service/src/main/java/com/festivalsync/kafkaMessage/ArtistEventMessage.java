package com.festivalsync.kafkaMessage;

import lombok.Data;

@Data
public class ArtistEventMessage {
    private Long artistId;
    private String desiredEventCity;
    private String desiredEventDate;
}

