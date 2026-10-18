package com.tickets.ticketservice.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class AsignarTecnicoRequestDTO {
    @NotNull(message = "Debes indicar el técnico que se asignará")
    private Integer idTecnico;
    @Size(max = 1000, message = "La descripción no puede superar 1000 caracteres")
    private String descripcion;
}
