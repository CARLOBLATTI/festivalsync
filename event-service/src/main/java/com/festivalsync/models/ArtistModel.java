package com.festivalsync.models;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "Modello che rappresenta un artista")
public class ArtistModel {

    @Schema(description = "ID univoco dell'artista", example = "1")
    private Long id;

    @Schema(description = "Nome dell'artista", example = "John Doe", required = true)
    @NotBlank(message = "Il nome dell'artista è obbligatorio")
    private String name;

    @Schema(description = "Genere musicale dell'artista", example = "Rock")
    private String genre;

    @Schema(description = "Paese di origine dell'artista", example = "Italy")
    private String country;

    @Schema(description = "Città di provenienza dell'artista", example = "Rome")
    private String location;

    @Schema(description = "Stato attuale dell'artista", example = "active")
    private String state;

    @Schema(description = "Numero di artisti che partecipano all'evento", example = "2")
    private int artistsNumber;

    @Schema(description = "Data di registrazione dell'artista", example = "2024-01-01")
    @NotNull(message = "La data di creazione è obbligatoria")
    private LocalDate creationDate;
}
