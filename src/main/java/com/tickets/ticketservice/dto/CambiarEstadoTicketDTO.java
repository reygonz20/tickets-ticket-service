package com.tickets.ticketservice.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class CambiarEstadoTicketDTO {
    @NotNull(message = "Debes indicar el nuevo estado")
    private Integer idEstado;
    @Size(max = 1000, message = "La descripción no puede superar 1000 caracteres")
    private String descripcion;
    @Size(max = 4000, message = "La solución no puede superar 4000 caracteres")
    private String solucion;
}
