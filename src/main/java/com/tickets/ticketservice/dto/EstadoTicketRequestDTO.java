package com.tickets.ticketservice.dto;

import jakarta.validation.constraints.*;

public record EstadoTicketRequestDTO(@NotBlank(message = "El nombre es obligatorio") @Size(max = 50) String nombre,
                                     @Size(max = 255) String descripcion,
                                     @NotNull(message = "El orden es obligatorio") @Min(value = 1, message = "El orden debe ser mayor a 0") Integer orden) {
}
