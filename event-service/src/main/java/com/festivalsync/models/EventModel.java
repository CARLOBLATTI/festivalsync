package com.festivalsync.models;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "Modello che rappresenta un evento")
public class EventModel {

    @Schema(description = "ID univoco dell'evento", example = "1")
    private Long id;

    @Schema(description = "Nome dell'evento", example = "Music Festival", required = true)
    @NotBlank(message = "Il nome dell'evento è obbligatorio")
    private String name;

    @Schema(description = "Data dell'evento", example = "2024-05-15", required = true)
    @NotNull(message = "La data dell'evento è obbligatoria")
    private LocalDate date;

    @Schema(description = "Luogo dell'evento", example = "Stadio Olimpico")
    private String location;

    @Schema(description = "Paese in cui si svolge l'evento", example = "Italy")
    private String country;

    @Schema(description = "Lista degli artisti che partecipano all'evento")
    private List<ArtistModel> artists;

    @Schema(description = "Stato dell'evento", example = "scheduled")
    private String state;

    @Schema(description = "Numero di artisti che partecipano all'evento", example = "2")
    private int artistsNumber;

    @Schema(description = "Data di creazione dell'evento", example = "2024-01-01")
    @NotNull(message = "La data di creazione è obbligatoria")
    private LocalDate creationDate;

    @Schema(description = "Timestamp di inserimento dell'evento", example = "2024-01-01T10:15:30")
    private LocalDateTime insertTimestamp;

    @Schema(description = "Timestamp di aggiornamento dell'evento", example = "2024-01-02T11:00:00")
    private LocalDateTime updateTimestamp;
}
