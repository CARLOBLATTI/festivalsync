package com.festivalsync.models.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Risposta per l'inserimento di un artista")
public class AddArtistResponse {

    @Schema(description = "ID dell'artista", example = "1")
    private Long id;

    @Schema(description = "Messaggio di conferma", example = "Artista aggiunto con successo")
    private String message;
}
