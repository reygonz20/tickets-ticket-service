package com.tickets.ticketservice.service;

import com.tickets.ticketservice.dto.*;
import com.tickets.ticketservice.entity.*;
import com.tickets.ticketservice.repository.*;
import com.tickets.ticketservice.security.AuthenticatedUserService;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
public class ComentarioTicketService {
    private final ComentarioTicketRepository comentarios;
    private final TicketRepository tickets;
    private final TicketService ticketService;
    private final AuthenticatedUserService actual;

    public ComentarioTicketService(ComentarioTicketRepository c, TicketRepository t, TicketService ts, AuthenticatedUserService a) {
        comentarios = c;
        tickets = t;
        ticketService = ts;
        actual = a;
    }

    @Transactional
    public ComentarioTicketResponseDTO crear(Integer idTicket, ComentarioTicketRequestDTO dto) {
        Ticket t = find(idTicket);
        ticketService.validarAcceso(t);
        ticketService.validarNoCerrado(t);
        ComentarioTicket c = new ComentarioTicket();
        c.setTicket(t);
        c.setIdUsuario(actual.idUsuario());
        c.setComentario(dto.comentario().trim());
        return convertir(comentarios.save(c));
    }

    @Transactional(readOnly = true)
    public List<ComentarioTicketResponseDTO> listar(Integer idTicket) {
        Ticket t = find(idTicket);
        ticketService.validarAcceso(t);
        return comentarios.findByTicket_IdTicketOrderByFechaComentarioAsc(idTicket).stream().map(this::convertir).toList();
    }

    private Ticket find(Integer id) {
        return tickets.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe el ticket con ID " + id));
    }

    private ComentarioTicketResponseDTO convertir(ComentarioTicket c) {
        UsuarioRemotoDto u = null;
        try {
            u = ticketService.validarUsuario(c.getIdUsuario());
        } catch (Exception ignored) {
        }
        return new ComentarioTicketResponseDTO(c.getIdComentario(), c.getTicket().getIdTicket(), c.getIdUsuario(), u != null ? u.nombreCompleto() : null, u != null ? u.rol() : null, c.getComentario(), c.getFechaComentario());
    }
}
