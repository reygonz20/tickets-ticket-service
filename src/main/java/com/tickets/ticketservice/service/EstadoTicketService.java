package com.tickets.ticketservice.service;

import com.tickets.ticketservice.dto.EstadoTicketRequestDTO;
import com.tickets.ticketservice.entity.EstadoTicket;
import com.tickets.ticketservice.repository.*;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
public class EstadoTicketService {
    private static final Set<String> BASE = Set.of("CREADO", "ASIGNADO", "EN_PROCESO", "PENDIENTE", "RESUELTO", "CERRADO", "CANCELADO");
    private final EstadoTicketRepository repo;
    private final TicketRepository tickets;

    public EstadoTicketService(EstadoTicketRepository r, TicketRepository t) {
        repo = r;
        tickets = t;
    }

    @Transactional(readOnly = true)
    public List<EstadoTicket> listarTodos() {
        return repo.findAllByOrderByOrdenAsc();
    }

    @Transactional(readOnly = true)
    public Optional<EstadoTicket> buscarPorId(Integer id) {
        return repo.findById(id);
    }

    @Transactional
    public EstadoTicket crear(EstadoTicketRequestDTO d) {
        String n = normalizar(d.nombre());
        if (repo.existsByNombreIgnoreCase(n)) throw bad("El estado ya existe");
        EstadoTicket e = new EstadoTicket();
        e.setNombre(n);
        e.setDescripcion(d.descripcion());
        e.setOrden(d.orden());
        return repo.save(e);
    }

    @Transactional
    public EstadoTicket actualizar(Integer id, EstadoTicketRequestDTO d) {
        EstadoTicket e = repo.findById(id).orElseThrow(() -> notFound());
        String nuevo = normalizar(d.nombre());
        if (BASE.contains(e.getNombre().toUpperCase()) && !e.getNombre().equalsIgnoreCase(nuevo))
            throw bad("No se puede cambiar el nombre funcional de un estado base");
        if (!e.getNombre().equalsIgnoreCase(nuevo) && repo.existsByNombreIgnoreCase(nuevo))
            throw bad("El estado ya existe");
        e.setNombre(nuevo);
        e.setDescripcion(d.descripcion());
        e.setOrden(d.orden());
        return repo.save(e);
    }

    @Transactional
    public void eliminar(Integer id) {
        EstadoTicket e = repo.findById(id).orElseThrow(() -> notFound());
        if (BASE.contains(e.getNombre().toUpperCase())) throw bad("No se puede eliminar un estado base del flujo");
        if (tickets.existsByEstadoTicket_IdEstado(id))
            throw bad("No se puede eliminar un estado utilizado por tickets");
        repo.delete(e);
    }

    private String normalizar(String s) {
        return s.trim().toUpperCase().replace(' ', '_');
    }

    private ResponseStatusException bad(String m) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, m);
    }

    private ResponseStatusException notFound() {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Estado no encontrado");
    }
}
