package com.tickets.ticketservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "historial_ticket")
@Getter
@Setter
@NoArgsConstructor
public class HistorialTicket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_historial")
    private Integer idHistorial;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_ticket", nullable = false)
    private Ticket ticket;

    // Identificador lógico del usuario que realizó la acción
    @Column(name = "id_usuario", nullable = false)
    private Integer idUsuario;

    @Column(name = "accion", nullable = false, length = 100)
    private String accion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estado_anterior")
    private EstadoTicket estadoAnterior;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estado_nuevo")
    private EstadoTicket estadoNuevo;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "fecha", nullable = false, updatable = false)
    private LocalDateTime fecha;

    @PrePersist
    protected void prePersist() {
        if (fecha == null) {
            fecha = LocalDateTime.now();
        }
    }
}
