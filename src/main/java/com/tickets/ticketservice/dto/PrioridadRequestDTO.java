package com.tickets.ticketservice.dto;

import jakarta.validation.constraints.*;

public record PrioridadRequestDTO(@NotBlank(message = "El nombre es obligatorio") @Size(max = 50) String nombre) {
}
