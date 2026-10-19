package com.tickets.ticketservice.repository;

import com.tickets.ticketservice.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;

public interface TicketRepository extends JpaRepository<Ticket, Integer> {
    List<Ticket> findAllByOrderByFechaCreacionDesc();

    List<Ticket> findByIdUsuarioCreadorOrderByFechaCreacionDesc(Integer idUsuario);

    List<Ticket> findByIdTecnicoAsignadoOrderByFechaCreacionDesc(Integer idTecnico);

    long countByIdTecnicoAsignado(Integer idTecnico);

    long countByIdTecnicoAsignadoAndEstadoTicket_NombreIn(Integer idTecnico, Collection<String> estados);

    boolean existsByCodigoTicket(String codigoTicket);

    boolean existsByCategoria_IdCategoria(Integer idCategoria);

    boolean existsByPrioridad_IdPrioridad(Integer idPrioridad);

    boolean existsByEstadoTicket_IdEstado(Integer idEstado);
}
