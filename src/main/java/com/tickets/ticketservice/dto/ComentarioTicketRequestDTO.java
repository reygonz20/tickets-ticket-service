package com.tickets.ticketservice.dto;

import jakarta.validation.constraints.*;

public record ComentarioTicketRequestDTO(
        @NotBlank(message = "El comentario es obligatorio") @Size(max = 4000, message = "El comentario no puede superar 4000 caracteres") String comentario) {
}
