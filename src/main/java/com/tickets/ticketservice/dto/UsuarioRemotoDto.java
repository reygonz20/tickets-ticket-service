package com.tickets.ticketservice.dto;

public record UsuarioRemotoDto(Integer idUsuario, String nombres, String apellidos, String correo, String telefono,
                               String rol, boolean estado) {
    public String nombreCompleto() {
        return (nombres == null ? "" : nombres) + (apellidos == null ? "" : " " + apellidos);
    }
}
