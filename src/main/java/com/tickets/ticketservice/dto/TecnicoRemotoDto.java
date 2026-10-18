package com.tickets.ticketservice.dto;

public record TecnicoRemotoDto(Integer idTecnico, Integer idUsuario, String nombres, String apellidos, String correo,
                               String telefono, Integer idEspecialidad, String especialidad, String disponibilidad,
                               boolean estado) {
    public String nombreCompleto() {
        return (nombres == null ? "" : nombres) + (apellidos == null ? "" : " " + apellidos);
    }
}
