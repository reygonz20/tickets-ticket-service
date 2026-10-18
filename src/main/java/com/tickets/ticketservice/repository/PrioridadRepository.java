package com.tickets.ticketservice.repository;

import com.tickets.ticketservice.entity.Prioridad;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrioridadRepository extends JpaRepository<Prioridad, Integer> {
    boolean existsByNombreIgnoreCase(String nombre);
}
