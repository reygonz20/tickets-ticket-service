package com.tickets.ticketservice.dto;

import java.time.LocalDateTime;

public record ComentarioTicketResponseDTO(Integer idComentario, Integer idTicket, Integer idUsuario,
                                          String nombreUsuario, String rolUsuario, String comentario,
                                          LocalDateTime fechaComentario) {
}
