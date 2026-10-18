package com.tickets.ticketservice.service;

import com.tickets.ticketservice.dto.CategoriaRequestDTO;
import com.tickets.ticketservice.entity.Categoria;
import com.tickets.ticketservice.repository.*;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
public class CategoriaService {
    private final CategoriaRepository repo;
    private final TicketRepository tickets;

    public CategoriaService(CategoriaRepository r, TicketRepository t) {
        repo = r;
        tickets = t;
    }

    @Transactional(readOnly = true)
    public List<Categoria> listarTodas() {
        return repo.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Categoria> buscarPorId(Integer id) {
        return repo.findById(id);
    }

    @Transactional
    public Categoria crear(CategoriaRequestDTO d) {
        if (repo.existsByNombreIgnoreCase(d.nombre().trim())) throw bad("La categoría ya existe");
        Categoria c = new Categoria();
        c.setNombre(d.nombre().trim());
        c.setDescripcion(d.descripcion());
        c.setEstado(d.estado() == null || d.estado());
        return repo.save(c);
    }

    @Transactional
    public Categoria actualizar(Integer id, CategoriaRequestDTO d) {
        Categoria c = repo.findById(id).orElseThrow(() -> notFound());
        if (!c.getNombre().equalsIgnoreCase(d.nombre().trim()) && repo.existsByNombreIgnoreCase(d.nombre().trim()))
            throw bad("La categoría ya existe");
        c.setNombre(d.nombre().trim());
        c.setDescripcion(d.descripcion());
        if (d.estado() != null) c.setEstado(d.estado());
        return repo.save(c);
    }

    @Transactional
    public void desactivar(Integer id) {
        Categoria c = repo.findById(id).orElseThrow(() -> notFound());
        c.setEstado(false);
        repo.save(c);
    }

    private ResponseStatusException bad(String m) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, m);
    }

    private ResponseStatusException notFound() {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoría no encontrada");
    }
}
