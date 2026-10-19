package com.tickets.ticketservice.security;

import io.jsonwebtoken.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.*;

public class JwtFilter extends OncePerRequestFilter {
    private final JwtProvider provider;

    public JwtFilter(JwtProvider p) {
        provider = p;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String a = request.getHeader("Authorization");
        SecurityContextHolder.clearContext();
        if (a != null && a.startsWith("Bearer ")) {
            try {
                Claims c = provider.verificar(a.substring(7));
                Object obj = c.get("authorities");
                if (c.getSubject() != null && obj instanceof List<?> roles) {
                    var granted = roles.stream().filter(String.class::isInstance).map(String.class::cast).map(SimpleGrantedAuthority::new).toList();
                    var auth = new UsernamePasswordAuthenticationToken(c.getSubject(), null, granted);
                    auth.setDetails(c.get("idUsuario", Integer.class));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            } catch (JwtException | IllegalArgumentException ex) {
                SecurityContextHolder.clearContext();
            }
        }
        chain.doFilter(request, response);
    }
}
