package com.tickets.ticketservice.security;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class AuthenticatedUserService {
    public Integer idUsuario() {
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        if (a == null || !a.isAuthenticated() || !(a.getDetails() instanceof Integer id))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no autenticado");
        return id;
    }

    public boolean hasRole(String role) {
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        return a != null && a.getAuthorities().stream().anyMatch(x -> x.getAuthority().equals("ROLE_" + role));
    }

    public String rol() {
        if (hasRole("ADMIN")) return "ADMIN";
        if (hasRole("TECNICO")) return "TECNICO";
        if (hasRole("USUARIO")) return "USUARIO";
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Rol no autorizado");
    }
}
