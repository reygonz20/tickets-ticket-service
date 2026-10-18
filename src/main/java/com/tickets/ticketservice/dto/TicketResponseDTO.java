package com.tickets.ticketservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class TicketResponseDTO {
    private Integer idTicket;
    private String codigoTicket;
    private String titulo;
    private String descripcion;
    private Integer idCategoria;
    private String categoria;
    private Integer idPrioridad;
    private String prioridad;
    private Integer idEstado;
    private String estado;
    private Integer idUsuarioCreador;
    private String usuarioCreador;
    private String correoUsuarioCreador;
    private Integer idTecnicoAsignado;
    private String tecnicoAsignado;
    private String correoTecnicoAsignado;
    private String solucion;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaAsignacion;
    private LocalDateTime fechaInicioAtencion;
    private LocalDateTime fechaResolucion;
    private LocalDateTime fechaCierre;
    private LocalDateTime fechaActualizacion;
}
