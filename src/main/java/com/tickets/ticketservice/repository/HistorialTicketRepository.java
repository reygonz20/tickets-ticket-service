package com.tickets.ticketservice.repository;

import com.tickets.ticketservice.entity.HistorialTicket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistorialTicketRepository
        extends JpaRepository<HistorialTicket, Integer> {

    List<HistorialTicket> findByTicket_IdTicketOrderByFechaAsc(
            Integer idTicket
    );
}
