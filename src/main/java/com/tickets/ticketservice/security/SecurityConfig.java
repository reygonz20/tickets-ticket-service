package com.tickets.ticketservice.security;

import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {
    @Bean
    SecurityFilterChain chain(HttpSecurity http, JwtProvider jwt) throws Exception {
        http.csrf(c -> c.disable()).sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(e -> e.authenticationEntryPoint((req, res, ex) -> res.sendError(401, "Token requerido o invalido"))
                        .accessDeniedHandler((req, res, ex) -> res.sendError(403, "Sin permiso para esta operacion")))
                .authorizeHttpRequests(a -> a
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/categorias/**", "/api/prioridades/**", "/api/estados-ticket/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/categorias/**", "/api/prioridades/**", "/api/estados-ticket/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/categorias/**", "/api/prioridades/**", "/api/estados-ticket/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/categorias/**", "/api/prioridades/**", "/api/estados-ticket/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/tickets/*/asignar-tecnico").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/tickets/estadisticas/tecnico/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/tickets").hasAnyRole("ADMIN", "USUARIO")
                        .requestMatchers("/api/tickets/**").authenticated()
                        .anyRequest().denyAll());
        http.addFilterBefore(new JwtFilter(jwt), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
