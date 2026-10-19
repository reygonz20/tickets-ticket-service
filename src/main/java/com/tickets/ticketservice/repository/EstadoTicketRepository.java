package com.tickets.ticketservice.repository;

import com.tickets.ticketservice.entity.EstadoTicket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;

public interface EstadoTicketRepository extends JpaRepository<EstadoTicket, Integer> {
    List<EstadoTicket> findAllByOrderByOrdenAsc();

    Optional<EstadoTicket> findByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCase(String nombre);
}
