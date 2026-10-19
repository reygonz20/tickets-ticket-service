package com.tickets.ticketservice.client;

import com.tickets.ticketservice.config.FeignAuthConfig;
import com.tickets.ticketservice.dto.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "tecnico-service", configuration = FeignAuthConfig.class)
public interface TecnicoClient {
    @GetMapping("/internal/tecnicos/{id}")
    RemoteGenericResponse<TecnicoRemotoDto> obtenerTecnico(@PathVariable("id") Integer id);

    @GetMapping("/internal/tecnicos/usuario/{idUsuario}")
    RemoteGenericResponse<TecnicoRemotoDto> obtenerPorUsuario(@PathVariable("idUsuario") Integer idUsuario);
}
