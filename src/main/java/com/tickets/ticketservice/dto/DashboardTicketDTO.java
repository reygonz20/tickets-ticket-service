package com.tickets.ticketservice.dto;

public record DashboardTicketDTO(long total, long creados, long asignados, long enProceso, long pendientes,
                                 long resueltos, long cerrados, long criticos) {
}
