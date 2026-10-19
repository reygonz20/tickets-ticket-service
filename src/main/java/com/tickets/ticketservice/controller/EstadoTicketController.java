package com.tickets.ticketservice.controller;

import com.tickets.ticketservice.dto.EstadoTicketRequestDTO;
import com.tickets.ticketservice.entity.EstadoTicket;
import com.tickets.ticketservice.service.EstadoTicketService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/estados-ticket")
public class EstadoTicketController {
    private final EstadoTicketService service;

    public EstadoTicketController(EstadoTicketService s) {
        service = s;
    }

    @GetMapping
    public ResponseEntity<List<EstadoTicket>> listar() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EstadoTicket> obtener(@PathVariable Integer id) {
        return service.buscarPorId(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<EstadoTicket> crear(@Valid @RequestBody EstadoTicketRequestDTO d) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(d));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EstadoTicket> actualizar(@PathVariable Integer id, @Valid @RequestBody EstadoTicketRequestDTO d) {
        return ResponseEntity.ok(service.actualizar(id, d));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
