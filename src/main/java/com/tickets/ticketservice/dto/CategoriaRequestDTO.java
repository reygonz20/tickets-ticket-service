package com.tickets.ticketservice.dto;

import jakarta.validation.constraints.*;

public record CategoriaRequestDTO(@NotBlank(message = "El nombre es obligatorio") @Size(max = 100) String nombre,
                                  @Size(max = 255) String descripcion, Boolean estado) {
}
