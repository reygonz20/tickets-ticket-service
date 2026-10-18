package com.tickets.ticketservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "comentarios_ticket")
@Getter
@Setter
@NoArgsConstructor
public class ComentarioTicket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_comentario")
    private Integer idComentario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_ticket", nullable = false)
    private Ticket ticket;

    // Identificador lógico del usuario en la tabla usuarios
    @Column(name = "id_usuario", nullable = false)
    private Integer idUsuario;

    @Column(name = "comentario", nullable = false, columnDefinition = "TEXT")
    private String comentario;

    @Column(name = "fecha_comentario", nullable = false, updatable = false)
    private LocalDateTime fechaComentario;

    @PrePersist
    protected void prePersist() {
        if (fechaComentario == null) {
            fechaComentario = LocalDateTime.now();
        }
    }
}
