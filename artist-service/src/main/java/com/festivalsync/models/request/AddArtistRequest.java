package com.festivalsync.models.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(description = "Richiesta per aggiungere un nuovo artista")
public class AddArtistRequest {

    @Schema(description = "Nome dell'artista", example = "John Doe", required = true)
    @NotBlank(message = "Il nome dell'artista è obbligatorio")
    private String name;

    @Schema(description = "Genere musicale dell'artista", example = "Rock")
    private String genre;

    @Schema(description = "Paese di origine dell'artista", example = "Italy")
    private String country;

    @Schema(description = "Città di provenienza dell'artista", example = "Rome")
    private String location;

    @Schema(description = "Città desiderata per l'evento", example = "Rome", required = true)
    @NotBlank(message = "La città desiderata è obbligatoria")
    private String desiredEventCity;

    @Schema(description = "Data desiderata per l'evento", example = "2024-05-15", required = true)
    @NotNull(message = "La data desiderata per l'evento è obbligatoria")
    private LocalDate desiredEventDate;
}
