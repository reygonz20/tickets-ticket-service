package com.tickets.ticketservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class HistorialTicketResponseDTO {
    private Integer idHistorial;
    private Integer idTicket;
    private Integer idUsuario;
    private String nombreUsuario;
    private String rolUsuario;
    private String accion;
    private Integer idEstadoAnterior;
    private String estadoAnterior;
    private Integer idEstadoNuevo;
    private String estadoNuevo;
    private String descripcion;
    private LocalDateTime fecha;
}
