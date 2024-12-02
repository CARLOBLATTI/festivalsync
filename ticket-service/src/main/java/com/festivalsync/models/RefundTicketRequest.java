package com.festivalsync.models;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Richiesta per rimborsare un ticket")
public class RefundTicketRequest {

    @Schema(description = "ID del ticket venduto da rimborsare", example = "1", required = true)
    @NotNull(message = "L'ID del ticket venduto è obbligatorio")
    private Long soldTicketId;
}
