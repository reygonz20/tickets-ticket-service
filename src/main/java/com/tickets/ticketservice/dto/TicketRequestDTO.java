package com.tickets.ticketservice.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class TicketRequestDTO {
    @NotBlank(message = "El título es obligatorio")
    @Size(max = 200, message = "El título no puede superar 200 caracteres")
    private String titulo;
    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 4000, message = "La descripción no puede superar 4000 caracteres")
    private String descripcion;
    @NotNull(message = "Debes seleccionar una categoría")
    private Integer idCategoria;
    @NotNull(message = "Debes seleccionar una prioridad")
    private Integer idPrioridad;
}
