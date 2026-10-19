package com.tickets.ticketservice.repository;

import com.tickets.ticketservice.entity.ComentarioTicket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ComentarioTicketRepository
        extends JpaRepository<ComentarioTicket, Integer> {

    List<ComentarioTicket> findByTicket_IdTicketOrderByFechaComentarioAsc(
            Integer idTicket
    );
}
