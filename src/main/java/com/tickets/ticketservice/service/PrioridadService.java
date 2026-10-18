package com.tickets.ticketservice.service;

import com.tickets.ticketservice.dto.PrioridadRequestDTO;
import com.tickets.ticketservice.entity.Prioridad;
import com.tickets.ticketservice.repository.*;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
public class PrioridadService {
    private final PrioridadRepository repo;
    private final TicketRepository tickets;

    public PrioridadService(PrioridadRepository r, TicketRepository t) {
        repo = r;
        tickets = t;
    }

    @Transactional(readOnly = true)
    public List<Prioridad> listarTodas() {
        return repo.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Prioridad> buscarPorId(Integer id) {
        return repo.findById(id);
    }

    @Transactional
    public Prioridad crear(PrioridadRequestDTO d) {
        String n = d.nombre().trim().toUpperCase();
        if (repo.existsByNombreIgnoreCase(n)) throw bad("La prioridad ya existe");
        Prioridad p = new Prioridad();
        p.setNombre(n);
        return repo.save(p);
    }

    @Transactional
    public Prioridad actualizar(Integer id, PrioridadRequestDTO d) {
        Prioridad p = repo.findById(id).orElseThrow(() -> notFound());
        String n = d.nombre().trim().toUpperCase();
        if (!p.getNombre().equalsIgnoreCase(n) && repo.existsByNombreIgnoreCase(n)) throw bad("La prioridad ya existe");
        p.setNombre(n);
        return repo.save(p);
    }

    @Transactional
    public void eliminar(Integer id) {
        if (tickets.existsByPrioridad_IdPrioridad(id))
            throw bad("No se puede eliminar una prioridad utilizada por tickets");
        if (!repo.existsById(id)) throw notFound();
        repo.deleteById(id);
    }

    private ResponseStatusException bad(String m) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, m);
    }

    private ResponseStatusException notFound() {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Prioridad no encontrada");
    }
}
