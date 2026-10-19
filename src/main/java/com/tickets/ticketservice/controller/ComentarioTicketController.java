package com.tickets.ticketservice.controller;

import com.tickets.ticketservice.dto.*;
import com.tickets.ticketservice.service.ComentarioTicketService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/tickets/{idTicket}/comentarios")
public class ComentarioTicketController {
    private final ComentarioTicketService service;

    public ComentarioTicketController(ComentarioTicketService s) {
        service = s;
    }

    @PostMapping
    public ResponseEntity<ComentarioTicketResponseDTO> crear(@PathVariable Integer idTicket, @Valid @RequestBody ComentarioTicketRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(idTicket, dto));
    }

    @GetMapping
    public ResponseEntity<List<ComentarioTicketResponseDTO>> listar(@PathVariable Integer idTicket) {
        return ResponseEntity.ok(service.listar(idTicket));
    }
}
