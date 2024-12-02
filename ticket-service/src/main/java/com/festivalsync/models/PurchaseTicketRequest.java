package com.festivalsync.models;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Richiesta per acquistare uno o più ticket")
public class PurchaseTicketRequest {

    @Schema(description = "ID del ticket da acquistare", example = "1", required = true)
    @NotNull(message = "L'ID del ticket è obbligatorio")
    private Long ticketId;

    @Schema(description = "Nome dell'acquirente", example = "John Doe", required = true)
    @NotBlank(message = "Il nome dell'acquirente è obbligatorio")
    private String buyerName;

    @Schema(description = "Email dell'acquirente", example = "johndoe@example.com", required = true)
    @NotBlank(message = "L'email dell'acquirente è obbligatoria")
    private String buyerEmail;

    @Schema(description = "Telefono dell'acquirente", example = "+1234567890", required = true)
    @NotBlank(message = "Il numero di telefono dell'acquirente è obbligatorio")
    private String buyerPhone;

    @Schema(description = "Quantità di ticket da acquistare", example = "2", required = true)
    @NotNull(message = "La quantità è obbligatoria")
    @Min(value = 1, message = "La quantità deve essere almeno 1")
    private Integer quantity;
}
