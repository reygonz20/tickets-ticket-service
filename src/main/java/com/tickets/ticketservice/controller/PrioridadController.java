package com.tickets.ticketservice.controller;

import com.tickets.ticketservice.dto.PrioridadRequestDTO;
import com.tickets.ticketservice.entity.Prioridad;
import com.tickets.ticketservice.service.PrioridadService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/prioridades")
public class PrioridadController {
    private final PrioridadService service;

    public PrioridadController(PrioridadService s) {
        service = s;
    }

    @GetMapping
    public ResponseEntity<List<Prioridad>> listar() {
        return ResponseEntity.ok(service.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Prioridad> obtener(@PathVariable Integer id) {
        return service.buscarPorId(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Prioridad> crear(@Valid @RequestBody PrioridadRequestDTO d) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(d));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Prioridad> actualizar(@PathVariable Integer id, @Valid @RequestBody PrioridadRequestDTO d) {
        return ResponseEntity.ok(service.actualizar(id, d));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
