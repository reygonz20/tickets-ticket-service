package com.tickets.ticketservice.dto;

public record RemoteGenericResponse<T>(String tipo, int rpta, String mensaje, T body) {
}
