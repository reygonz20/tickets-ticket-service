package com.tickets.ticketservice.service;

import com.tickets.ticketservice.client.*;
import com.tickets.ticketservice.dto.*;
import com.tickets.ticketservice.entity.*;
import com.tickets.ticketservice.repository.*;
import com.tickets.ticketservice.security.AuthenticatedUserService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.*;
import java.util.*;

@Service
public class TicketService {
    private final TicketRepository tickets;
    private final CategoriaRepository categorias;
    private final PrioridadRepository prioridades;
    private final EstadoTicketRepository estados;
    private final HistorialTicketRepository historial;
    private final AuthClient auth;
    private final TecnicoClient tecnicos;
    private final AuthenticatedUserService actual;

    public TicketService(TicketRepository t, CategoriaRepository c, PrioridadRepository p, EstadoTicketRepository e, HistorialTicketRepository h, AuthClient a, TecnicoClient te, AuthenticatedUserService au) {
        tickets = t;
        categorias = c;
        prioridades = p;
        estados = e;
        historial = h;
        auth = a;
        tecnicos = te;
        actual = au;
    }

    @Transactional(readOnly = true)
    public List<TicketResponseDTO> listarVisibles() {
        String rol = actual.rol();
        Integer id = actual.idUsuario();
        List<Ticket> lista;
        if ("ADMIN".equals(rol)) lista = tickets.findAllByOrderByFechaCreacionDesc();
        else if ("USUARIO".equals(rol)) lista = tickets.findByIdUsuarioCreadorOrderByFechaCreacionDesc(id);
        else {
            TecnicoRemotoDto te = tecnicoActual();
            lista = tickets.findByIdTecnicoAsignadoOrderByFechaCreacionDesc(te.idTecnico());
        }
        return lista.stream().map(this::convertirADTO).toList();
    }

    @Transactional(readOnly = true)
    public TicketResponseDTO buscarPorId(Integer id) {
        Ticket t = find(id);
        validarAcceso(t);
        return convertirADTO(t);
    }

    @Transactional
    public TicketResponseDTO crear(TicketRequestDTO dto) {
        if (actual.hasRole("TECNICO")) throw forbidden("Los técnicos no registran tickets");
        Integer actor = actual.idUsuario();
        UsuarioRemotoDto u = validarUsuario(actor);
        if (!u.estado()) throw bad("El usuario está inactivo");
        Categoria c = categorias.findById(dto.getIdCategoria()).orElseThrow(() -> bad("La categoría indicada no existe"));
        if (Boolean.FALSE.equals(c.getEstado())) throw bad("La categoría está inactiva");
        Prioridad p = prioridades.findById(dto.getIdPrioridad()).orElseThrow(() -> bad("La prioridad indicada no existe"));
        EstadoTicket creado = estado("CREADO");
        Ticket t = new Ticket();
        t.setCodigoTicket(generarCodigoTicket());
        t.setTitulo(dto.getTitulo().trim());
        t.setDescripcion(dto.getDescripcion().trim());
        t.setCategoria(c);
        t.setPrioridad(p);
        t.setEstadoTicket(creado);
        t.setIdUsuarioCreador(actor);
        Ticket g = tickets.save(t);
        registrarHistorial(g, actor, "CREACION", null, creado, "Ticket registrado por el usuario.");
        return convertirADTO(g);
    }

    @Transactional
    public TicketResponseDTO actualizar(Integer id, TicketUpdateRequestDTO dto) {
        Ticket t = find(id);
        validarAcceso(t);
        validarNoCerrado(t);
        if (actual.hasRole("TECNICO")) throw forbidden("El técnico no puede editar los datos generales del ticket");
        if (actual.hasRole("USUARIO") && !"CREADO".equalsIgnoreCase(t.getEstadoTicket().getNombre()))
            throw forbidden("El usuario solo puede editar un ticket mientras está en estado CREADO");
        Categoria c = categorias.findById(dto.getIdCategoria()).orElseThrow(() -> bad("La categoría indicada no existe"));
        if (Boolean.FALSE.equals(c.getEstado())) throw bad("La categoría está inactiva");
        Prioridad p = prioridades.findById(dto.getIdPrioridad()).orElseThrow(() -> bad("La prioridad indicada no existe"));
        t.setTitulo(dto.getTitulo().trim());
        t.setDescripcion(dto.getDescripcion().trim());
        t.setCategoria(c);
        t.setPrioridad(p);
        return convertirADTO(tickets.save(t));
    }

    @Transactional
    public TicketResponseDTO cambiarEstado(Integer id, CambiarEstadoTicketDTO dto) {
        Ticket t = find(id);
        Integer actor = actual.idUsuario();
        EstadoTicket anterior = t.getEstadoTicket();
        EstadoTicket nuevo = estados.findById(dto.getIdEstado()).orElseThrow(() -> bad("El estado indicado no existe"));
        if (actual.hasRole("USUARIO")) throw forbidden("El usuario no puede cambiar el estado del ticket");
        if (actual.hasRole("TECNICO")) {
            validarTecnicoAsignado(t);
            if ("CERRADO".equalsIgnoreCase(anterior.getNombre()))
                throw forbidden("Solo un administrador puede reabrir un ticket cerrado");
            if ("CERRADO".equalsIgnoreCase(nuevo.getNombre()))
                throw forbidden("Solo un administrador puede cerrar un ticket");
        }
        if ("CERRADO".equalsIgnoreCase(anterior.getNombre()) && !actual.hasRole("ADMIN"))
            throw forbidden("Solo un administrador puede reabrir un ticket cerrado");
        if (Objects.equals(anterior.getIdEstado(), nuevo.getIdEstado()))
            throw bad("El ticket ya se encuentra en ese estado");
        if ("RESUELTO".equalsIgnoreCase(nuevo.getNombre()) && (dto.getSolucion() == null || dto.getSolucion().isBlank()))
            throw bad("Debe registrar la solución para marcar el ticket como resuelto");
        LocalDateTime ahora = LocalDateTime.now();
        String n = nuevo.getNombre();
        if ("EN_PROCESO".equalsIgnoreCase(n) && t.getFechaInicioAtencion() == null) t.setFechaInicioAtencion(ahora);
        if ("RESUELTO".equalsIgnoreCase(n)) {
            if (t.getFechaResolucion() == null) t.setFechaResolucion(ahora);
            t.setSolucion(dto.getSolucion().trim());
        }
        if ("CERRADO".equalsIgnoreCase(n)) {
            if (!actual.hasRole("ADMIN")) throw forbidden("Solo un administrador puede cerrar el ticket");
            if (t.getFechaCierre() == null) t.setFechaCierre(ahora);
        }
        if ("CERRADO".equalsIgnoreCase(anterior.getNombre()) && actual.hasRole("ADMIN")) {
            t.setFechaCierre(null);
            if (!"RESUELTO".equalsIgnoreCase(n)) t.setFechaResolucion(null);
        }
        t.setEstadoTicket(nuevo);
        Ticket g = tickets.save(t);
        registrarHistorial(g, actor, "CAMBIO_ESTADO", anterior, nuevo, dto.getDescripcion());
        return convertirADTO(g);
    }

    @Transactional
    public TicketResponseDTO asignarTecnico(Integer id, AsignarTecnicoRequestDTO dto) {
        if (!actual.hasRole("ADMIN")) throw forbidden("Solo el administrador puede asignar técnicos");
        Ticket t = find(id);
        validarNoCerrado(t);
        TecnicoRemotoDto te = validarTecnico(dto.getIdTecnico(), true);
        if (Objects.equals(t.getIdTecnicoAsignado(), te.idTecnico()))
            throw bad("El ticket ya está asignado a ese técnico");
        EstadoTicket anterior = t.getEstadoTicket();
        t.setIdTecnicoAsignado(te.idTecnico());
        if (t.getFechaAsignacion() == null) t.setFechaAsignacion(LocalDateTime.now());
        if ("CREADO".equalsIgnoreCase(anterior.getNombre())) t.setEstadoTicket(estado("ASIGNADO"));
        Ticket g = tickets.save(t);
        String d = dto.getDescripcion() != null && !dto.getDescripcion().isBlank() ? dto.getDescripcion().trim() : "Ticket asignado a " + te.nombreCompleto();
        registrarHistorial(g, actual.idUsuario(), "ASIGNACION", anterior, g.getEstadoTicket(), d);
        return convertirADTO(g);
    }

    @Transactional(readOnly = true)
    public List<HistorialTicketResponseDTO> listarHistorial(Integer id) {
        Ticket t = find(id);
        validarAcceso(t);
        return historial.findByTicket_IdTicketOrderByFechaAsc(id).stream().map(this::convertirHistorialADTO).toList();
    }

    @Transactional(readOnly = true)
    public DashboardTicketDTO dashboard() {
        List<Ticket> l;
        if (actual.hasRole("ADMIN")) l = tickets.findAll();
        else if (actual.hasRole("USUARIO"))
            l = tickets.findByIdUsuarioCreadorOrderByFechaCreacionDesc(actual.idUsuario());
        else l = tickets.findByIdTecnicoAsignadoOrderByFechaCreacionDesc(tecnicoActual().idTecnico());
        return new DashboardTicketDTO(l.size(), count(l, "CREADO"), count(l, "ASIGNADO"), count(l, "EN_PROCESO"), count(l, "PENDIENTE"), count(l, "RESUELTO"), count(l, "CERRADO"), l.stream().filter(x -> "CRITICA".equalsIgnoreCase(x.getPrioridad().getNombre())).count());
    }

    @Transactional(readOnly = true)
    public TecnicoEstadisticaDTO estadisticaTecnico(Integer idTecnico) {
        if (!actual.hasRole("ADMIN")) throw forbidden("Solo el administrador puede consultar esta estadística");
        long asignados = tickets.countByIdTecnicoAsignado(idTecnico);
        long resueltos = tickets.countByIdTecnicoAsignadoAndEstadoTicket_NombreIn(idTecnico, List.of("RESUELTO", "CERRADO"));
        return new TecnicoEstadisticaDTO(idTecnico, asignados, resueltos);
    }

    public void validarAcceso(Ticket t) {
        if (actual.hasRole("ADMIN")) return;
        if (actual.hasRole("USUARIO")) {
            if (!Objects.equals(t.getIdUsuarioCreador(), actual.idUsuario()))
                throw forbidden("No tiene permiso para acceder a este ticket");
            return;
        }
        if (actual.hasRole("TECNICO")) {
            validarTecnicoAsignado(t);
            return;
        }
        throw forbidden("Rol no autorizado");
    }

    public void validarNoCerrado(Ticket t) {
        if ("CERRADO".equalsIgnoreCase(t.getEstadoTicket().getNombre()))
            throw bad("El ticket está cerrado y no admite modificaciones. Un administrador debe reabrirlo primero.");
    }

    private void validarTecnicoAsignado(Ticket t) {
        TecnicoRemotoDto te = tecnicoActual();
        if (t.getIdTecnicoAsignado() == null || !Objects.equals(t.getIdTecnicoAsignado(), te.idTecnico()))
            throw forbidden("El ticket no está asignado al técnico autenticado");
    }

    private TecnicoRemotoDto tecnicoActual() {
        try {
            var r = tecnicos.obtenerPorUsuario(actual.idUsuario());
            var t = r == null ? null : r.body();
            if (t == null || !t.estado())
                throw forbidden("No existe un perfil técnico activo para el usuario autenticado");
            return t;
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "No se pudo consultar el perfil técnico");
        }
    }

    private long count(List<Ticket> l, String estado) {
        return l.stream().filter(x -> estado.equalsIgnoreCase(x.getEstadoTicket().getNombre())).count();
    }

    private String generarCodigoTicket() {
        String pref = "TK-" + Year.now().getValue() + "-";
        int next = tickets.findAll().stream().map(Ticket::getIdTicket).filter(Objects::nonNull).max(Integer::compareTo).orElse(0) + 1;
        String c;
        do {
            c = pref + String.format("%06d", next++);
        } while (tickets.existsByCodigoTicket(c));
        return c;
    }

    private Ticket find(Integer id) {
        return tickets.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe el ticket con ID " + id));
    }

    private EstadoTicket estado(String nombre) {
        return estados.findByNombreIgnoreCase(nombre).orElseThrow(() -> bad("No existe el estado " + nombre));
    }

    private void registrarHistorial(Ticket t, Integer id, String accion, EstadoTicket a, EstadoTicket n, String d) {
        HistorialTicket h = new HistorialTicket();
        h.setTicket(t);
        h.setIdUsuario(id);
        h.setAccion(accion);
        h.setEstadoAnterior(a);
        h.setEstadoNuevo(n);
        h.setDescripcion(d);
        historial.save(h);
    }

    public UsuarioRemotoDto validarUsuario(Integer id) {
        try {
            var r = auth.obtenerUsuario(id);
            var u = r == null ? null : r.body();
            if (u == null) throw bad("El usuario indicado no existe");
            return u;
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "No se pudo validar el usuario en auth-service");
        }
    }

    private TecnicoRemotoDto validarTecnico(Integer id, boolean disponible) {
        try {
            var r = tecnicos.obtenerTecnico(id);
            var t = r == null ? null : r.body();
            if (t == null) throw bad("El técnico indicado no existe");
            if (!t.estado()) throw bad("El técnico está inactivo");
            if (disponible && !"DISPONIBLE".equalsIgnoreCase(t.disponibilidad()))
                throw bad("El técnico no está disponible");
            return t;
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "No se pudo validar el técnico en tecnico-service");
        }
    }

    private TicketResponseDTO convertirADTO(Ticket t) {
        UsuarioRemotoDto u = null;
        TecnicoRemotoDto te = null;
        try {
            u = validarUsuario(t.getIdUsuarioCreador());
        } catch (Exception ignored) {
        }
        if (t.getIdTecnicoAsignado() != null) try {
            te = validarTecnico(t.getIdTecnicoAsignado(), false);
        } catch (Exception ignored) {
        }
        return new TicketResponseDTO(t.getIdTicket(), t.getCodigoTicket(), t.getTitulo(), t.getDescripcion(), t.getCategoria().getIdCategoria(), t.getCategoria().getNombre(), t.getPrioridad().getIdPrioridad(), t.getPrioridad().getNombre(), t.getEstadoTicket().getIdEstado(), t.getEstadoTicket().getNombre(), t.getIdUsuarioCreador(), u != null ? u.nombreCompleto() : null, u != null ? u.correo() : null, t.getIdTecnicoAsignado(), te != null ? te.nombreCompleto() : null, te != null ? te.correo() : null, t.getSolucion(), t.getFechaCreacion(), t.getFechaAsignacion(), t.getFechaInicioAtencion(), t.getFechaResolucion(), t.getFechaCierre(), t.getFechaActualizacion());
    }

    private HistorialTicketResponseDTO convertirHistorialADTO(HistorialTicket h) {
        UsuarioRemotoDto u = null;
        try {
            u = validarUsuario(h.getIdUsuario());
        } catch (Exception ignored) {
        }
        EstadoTicket a = h.getEstadoAnterior(), n = h.getEstadoNuevo();
        return new HistorialTicketResponseDTO(h.getIdHistorial(), h.getTicket().getIdTicket(), h.getIdUsuario(), u != null ? u.nombreCompleto() : null, u != null ? u.rol() : null, h.getAccion(), a != null ? a.getIdEstado() : null, a != null ? a.getNombre() : null, n != null ? n.getIdEstado() : null, n != null ? n.getNombre() : null, h.getDescripcion(), h.getFecha());
    }

    private ResponseStatusException bad(String m) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, m);
    }

    private ResponseStatusException forbidden(String m) {
        return new ResponseStatusException(HttpStatus.FORBIDDEN, m);
    }
}
