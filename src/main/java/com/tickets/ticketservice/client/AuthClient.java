package com.tickets.ticketservice.client;

import com.tickets.ticketservice.config.FeignAuthConfig;
import com.tickets.ticketservice.dto.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "auth-service", configuration = FeignAuthConfig.class)
public interface AuthClient {
    @GetMapping("/internal/usuarios/{id}")
    RemoteGenericResponse<UsuarioRemotoDto> obtenerUsuario(@PathVariable("id") Integer id);
}
