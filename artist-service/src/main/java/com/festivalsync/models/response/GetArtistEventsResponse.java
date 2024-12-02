package com.festivalsync.models.response;

import com.festivalsync.models.ArtistModel;
import com.festivalsync.models.EventModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "Risposta per un artista e gli eventi a cui partecipa")
public class GetArtistEventsResponse {

    @Schema(description = "Artista", example = "1")
    private ArtistModel artistModel;

    @Schema(description = "Lista degli eventi a cui partecipa l'artista")
    private List<EventModel> events;
}

