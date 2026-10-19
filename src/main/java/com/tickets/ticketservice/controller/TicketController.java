package com.tickets.ticketservice.controller;

import com.tickets.ticketservice.dto.*;
import com.tickets.ticketservice.service.TicketService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {
    private final TicketService service;

    public TicketController(TicketService s) {
        service = s;
    }

    @GetMapping
    public ResponseEntity<List<TicketResponseDTO>> listar() {
        return ResponseEntity.ok(service.listarVisibles());
    }

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardTicketDTO> dashboard() {
        return ResponseEntity.ok(service.dashboard());
    }

    @GetMapping("/estadisticas/tecnico/{idTecnico}")
    public ResponseEntity<TecnicoEstadisticaDTO> estadistica(@PathVariable Integer idTecnico) {
        return ResponseEntity.ok(service.estadisticaTecnico(idTecnico));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketResponseDTO> obtener(@PathVariable Integer id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<TicketResponseDTO> crear(@Valid @RequestBody TicketRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TicketResponseDTO> actualizar(@PathVariable Integer id, @Valid @RequestBody TicketUpdateRequestDTO dto) {
        return ResponseEntity.ok(service.actualizar(id, dto));
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<TicketResponseDTO> cambiarEstado(@PathVariable Integer id, @Valid @RequestBody CambiarEstadoTicketDTO dto) {
        return ResponseEntity.ok(service.cambiarEstado(id, dto));
    }

    @PutMapping("/{id}/asignar-tecnico")
    public ResponseEntity<TicketResponseDTO> asignar(@PathVariable Integer id, @Valid @RequestBody AsignarTecnicoRequestDTO dto) {
        return ResponseEntity.ok(service.asignarTecnico(id, dto));
    }

    @GetMapping("/{id}/historial")
    public ResponseEntity<List<HistorialTicketResponseDTO>> historial(@PathVariable Integer id) {
        return ResponseEntity.ok(service.listarHistorial(id));
    }
}
