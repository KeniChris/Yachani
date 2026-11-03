package com.yachaniapi.grupoestudio.service;

import com.yachaniapi.grupoestudio.dto.GrupoEstudioDTO;
import com.yachaniapi.grupoestudio.entity.GrupoEstudio;
import com.yachaniapi.grupoestudio.entity.SolicitudGrupo;
import com.yachaniapi.grupoestudio.entity.TipoGrupo;
import com.yachaniapi.grupoestudio.exception.GrupoEstudioExceptions.*;
import com.yachaniapi.grupoestudio.repository.GrupoEstudioRepository;
import com.yachaniapi.grupoestudio.repository.SolicitudGrupoRepository;
import com.yachaniapi.notificacion.service.NotificacionService;
import com.yachaniapi.usuario.entity.Estudiante;
import com.yachaniapi.usuario.entity.Tutor;
import com.yachaniapi.usuario.entity.Usuario;
import com.yachaniapi.usuario.exception.UsuarioNoEncontradoException;
import com.yachaniapi.usuario.exception.UsuarioNoEsEstudianteException;
import com.yachaniapi.usuario.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GrupoEstudioService {

    private static final int MAX_GRUPOS_TUTOR = 10;
    private static final int MAX_GRUPOS_ESTUDIANTE = 5;

    private final GrupoEstudioRepository grupoRepository;
    private final SolicitudGrupoRepository solicitudRepository;
    private final UsuarioRepository usuarioRepository;
    private final NotificacionService notificacionService;

    public GrupoEstudioService(
            GrupoEstudioRepository grupoRepository,
            SolicitudGrupoRepository solicitudRepository,
            UsuarioRepository usuarioRepository,
            NotificacionService notificacionService) {
        this.grupoRepository = grupoRepository;
        this.solicitudRepository = solicitudRepository;
        this.usuarioRepository = usuarioRepository;
        this.notificacionService = notificacionService;
    }

    @Transactional
    public GrupoEstudioDTO.Response crearGrupo(GrupoEstudioDTO.CrearRequest request) {
        Usuario usuario = usuarioRepository.findById(request.getIdCreador())
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado"));

        TipoGrupo tipoGrupo;
        int maxGrupos;
        if (usuario instanceof Tutor) {
            tipoGrupo = TipoGrupo.TUTOR;
            maxGrupos = MAX_GRUPOS_TUTOR;
        } else if (usuario instanceof Estudiante) {
            tipoGrupo = TipoGrupo.ESTUDIANTES;
            maxGrupos = MAX_GRUPOS_ESTUDIANTE;
        } else {
            throw new IllegalArgumentException("Solo tutores o estudiantes pueden crear grupos");
        }

        if (grupoRepository.countByCreador_IdUsuario(usuario.getIdUsuario()) >= maxGrupos) {
            throw new IllegalArgumentException("Alcanzaste el máximo de " + maxGrupos + " grupos de estudio");
        }

        GrupoEstudio grupo = new GrupoEstudio();
        grupo.setNombre(request.getNombre().trim());
        grupo.setDescripcion(request.getDescripcion().trim());
        grupo.setMetodoEstudio(request.getMetodoEstudio().trim());
        grupo.setCapacidadMaxima(request.getCapacidadMaxima());
        grupo.setEstado("ACTIVO");
        grupo.setTipoGrupo(tipoGrupo);
        grupo.setCreador(usuario);

        GrupoEstudio guardado = grupoRepository.save(grupo);
        return toResponse(guardado, "Grupo creado correctamente");
    }

    @Transactional
    public GrupoEstudioDTO.Response actualizarCapacidad(Long idGrupo, GrupoEstudioDTO.EditarCapacidadRequest request) {
        GrupoEstudio grupo = grupoRepository.findById(idGrupo)
                .orElseThrow(() -> new GrupoNoEncontradoException("El grupo de estudio no existe"));

        int actuales = (grupo.getParticipantes() != null) ? grupo.getParticipantes().size() : 0;
        if (request.getNuevaCapacidad() < actuales) {
            throw new IllegalArgumentException("El nuevo límite no puede ser menor a los alumnos inscritos (" + actuales + ")");
        }

        grupo.setCapacidadMaxima(request.getNuevaCapacidad());
        GrupoEstudio guardado = grupoRepository.save(grupo);
        return toResponse(guardado, "Capacidad actualizada");
    }

    @Transactional
    public GrupoEstudioDTO.SolicitudResponse solicitarUnirse(Long idGrupo, GrupoEstudioDTO.SolicitudRequest request) {
        GrupoEstudio grupo = grupoRepository.findById(idGrupo)
                .orElseThrow(() -> new GrupoNoEncontradoException("El grupo de estudio no existe"));

        Usuario usuario = usuarioRepository.findById(request.getIdEstudiante())
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado"));

        if (!(usuario instanceof Estudiante estudiante)) {
            throw new UsuarioNoEsEstudianteException("Solo los estudiantes pueden unirse a los grupos");
        }

        if (grupo.getCreador().getIdUsuario().equals(estudiante.getIdUsuario())) {
            throw new IllegalArgumentException("El creador del grupo no puede solicitar unirse a su propio grupo");
        }

        int actuales = (grupo.getParticipantes() != null) ? grupo.getParticipantes().size() : 0;
        if (actuales >= grupo.getCapacidadMaxima()) {
            throw new GrupoLlenoException("El grupo ya alcanzó su capacidad máxima");
        }

        if (grupo.getParticipantes().contains(estudiante)) {
            throw new IllegalArgumentException("El estudiante ya es miembro del grupo");
        }

        // Solo bloquea si hay una solicitud pendiente. Si la anterior fue rechazada, o fue
        // aceptada y luego lo sacaron del grupo, puede volver a solicitar.
        if (solicitudRepository.existsByGrupo_IdGrupoAndEstudiante_IdUsuarioAndEstado(
                idGrupo, estudiante.getIdUsuario(), "PENDIENTE")) {
            throw new IllegalArgumentException("Ya tienes una solicitud pendiente en este grupo");
        }

        SolicitudGrupo solicitud = new SolicitudGrupo();
        solicitud.setGrupo(grupo);
        solicitud.setEstudiante(estudiante);
        solicitud.setEstado("PENDIENTE");

        SolicitudGrupo guardada = solicitudRepository.save(solicitud);
        notificacionService.notificarSolicitudRecibida(guardada);
        return toSolicitudResponse(guardada, "Solicitud enviada correctamente");
    }

    @Transactional
    public GrupoEstudioDTO.SolicitudResponse aceptarSolicitud(Long idSolicitud) {
        SolicitudGrupo solicitud = solicitudRepository.findById(idSolicitud)
                .orElseThrow(() -> new SolicitudNoEncontradaException("La solicitud no existe"));

        if (!"PENDIENTE".equals(solicitud.getEstado())) {
            throw new IllegalArgumentException("La solicitud ya fue procesada");
        }

        GrupoEstudio grupo = solicitud.getGrupo();
        int actuales = (grupo.getParticipantes() != null) ? grupo.getParticipantes().size() : 0;
        if (actuales >= grupo.getCapacidadMaxima()) {
            throw new GrupoLlenoException("Grupo lleno, no se pueden aceptar más solicitudes");
        }

        solicitud.setEstado("ACEPTADA");
        grupo.getParticipantes().add(solicitud.getEstudiante());
        grupoRepository.save(grupo);
        notificacionService.notificarSolicitudAceptada(solicitud);

        // La solicitud aceptada ya cumplió su función: se borra para que no quede en
        // "Mis solicitudes" y para que el estudiante pueda volver a solicitar si algún día
        // lo sacan del grupo. Antes hay que soltar la referencia desde las notificaciones.
        GrupoEstudioDTO.SolicitudResponse respuesta = toSolicitudResponse(solicitud, "Solicitud aceptada");
        notificacionService.desvincularDeSolicitud(solicitud.getIdSolicitud());
        solicitudRepository.delete(solicitud);

        return respuesta;
    }

    @Transactional
    public GrupoEstudioDTO.SolicitudResponse rechazarSolicitud(Long idSolicitud) {
        SolicitudGrupo solicitud = solicitudRepository.findById(idSolicitud)
                .orElseThrow(() -> new SolicitudNoEncontradaException("La solicitud no existe"));

        if (!"PENDIENTE".equals(solicitud.getEstado())) {
            throw new IllegalArgumentException("La solicitud ya fue procesada");
        }

        solicitud.setEstado("RECHAZADA");
        solicitudRepository.save(solicitud);
        notificacionService.notificarSolicitudRechazada(solicitud);
        return toSolicitudResponse(solicitud, "Solicitud rechazada");
    }

    public GrupoEstudioDTO.Response obtenerGrupo(Long idGrupo) {
        GrupoEstudio grupo = grupoRepository.findById(idGrupo)
                .orElseThrow(() -> new GrupoNoEncontradoException("El grupo de estudio no existe"));
        return toResponse(grupo, null);
    }

    public List<GrupoEstudioDTO.Response> listarGruposDeUsuario(Long idUsuario) {
        if (!usuarioRepository.existsById(idUsuario)) {
            throw new UsuarioNoEncontradoException("Usuario no encontrado");
        }
        return grupoRepository.findByUsuario(idUsuario).stream()
                .map(g -> toResponse(g, null))
                .collect(Collectors.toList());
    }

    public List<GrupoEstudioDTO.IntegranteResponse> listarIntegrantes(Long idGrupo) {
        GrupoEstudio grupo = grupoRepository.findById(idGrupo)
                .orElseThrow(() -> new GrupoNoEncontradoException("El grupo de estudio no existe"));
        return grupo.getParticipantes().stream()
                .map(estudiante -> new GrupoEstudioDTO.IntegranteResponse(
                        estudiante.getIdUsuario(),
                        estudiante.getNombres() + " " + estudiante.getApellidos(),
                        estudiante.getCorreo()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void eliminarIntegrante(Long idGrupo, Long idEstudiante) {
        GrupoEstudio grupo = grupoRepository.findById(idGrupo)
                .orElseThrow(() -> new GrupoNoEncontradoException("El grupo de estudio no existe"));

        boolean eliminado = grupo.getParticipantes().removeIf(
                estudiante -> estudiante.getIdUsuario().equals(idEstudiante));

        if (!eliminado) {
            throw new IntegranteNoEncontradoException("El estudiante no es integrante de este grupo");
        }

        grupoRepository.save(grupo);
    }

    public List<GrupoEstudioDTO.SolicitudPendienteResponse> listarSolicitudesPendientes(Long idGrupo) {
        if (!grupoRepository.existsById(idGrupo)) {
            throw new GrupoNoEncontradoException("El grupo de estudio no existe");
        }
        return solicitudRepository.findByGrupo_IdGrupoAndEstadoOrderByFechaSolicitudAsc(idGrupo, "PENDIENTE").stream()
                .map(solicitud -> new GrupoEstudioDTO.SolicitudPendienteResponse(
                        solicitud.getIdSolicitud(),
                        solicitud.getGrupo().getIdGrupo(),
                        solicitud.getEstudiante().getIdUsuario(),
                        solicitud.getEstudiante().getNombres() + " " + solicitud.getEstudiante().getApellidos(),
                        solicitud.getEstado(),
                        solicitud.getFechaSolicitud()))
                .collect(Collectors.toList());
    }

    @Transactional
    public GrupoEstudioDTO.Response editarNombre(Long idGrupo, String nombre) {
        GrupoEstudio grupo = grupoRepository.findById(idGrupo)
                .orElseThrow(() -> new GrupoNoEncontradoException("El grupo de estudio no existe"));
        grupo.setNombre(nombre.trim());
        return toResponse(grupoRepository.save(grupo), "Nombre actualizado");
    }

    @Transactional
    public GrupoEstudioDTO.Response cambiarPermitirPublicaciones(Long idGrupo, Boolean permitirPublicaciones) {
        GrupoEstudio grupo = grupoRepository.findById(idGrupo)
                .orElseThrow(() -> new GrupoNoEncontradoException("El grupo de estudio no existe"));
        grupo.setPermitirPublicaciones(permitirPublicaciones);
        return toResponse(grupoRepository.save(grupo), "Configuración actualizada");
    }

    public List<GrupoEstudioDTO.Response> buscarGrupos(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new IllegalArgumentException("Ingrese un curso o tema");
        }

        String busqueda = normalizar(keyword);

        // La comparación se hace en memoria para ignorar tildes: "calculo" encuentra
        // "Dominando Cálculo".
        return grupoRepository.findAll().stream()
                .filter(g -> normalizar(g.getNombre()).contains(busqueda)
                        || normalizar(g.getDescripcion()).contains(busqueda))
                .map(g -> toResponse(g, "Búsqueda completada"))
                .collect(Collectors.toList());
    }

    public List<GrupoEstudioDTO.Response> filtrarGrupos(String curso, String metodoEstudio, Integer cantidadIntegrantes, TipoGrupo tipoGrupo) {
        // Los filtros de texto se aplican en memoria para ignorar tildes y mayúsculas;
        // un filtro vacío no filtra, así que sin ningún filtro devuelve todos los grupos.
        String cursoFiltro = normalizar(curso);
        String metodoFiltro = normalizar(metodoEstudio);

        return grupoRepository.findByFiltros(cantidadIntegrantes, tipoGrupo).stream()
                .filter(g -> cursoFiltro.isEmpty() || normalizar(g.getNombre()).contains(cursoFiltro))
                .filter(g -> metodoFiltro.isEmpty() || normalizar(g.getMetodoEstudio()).equals(metodoFiltro))
                .map(g -> toResponse(g, "Filtrado completado"))
                .collect(Collectors.toList());
    }

    // Pasa el texto a minúsculas y le quita las tildes, para comparar sin importar
    // acentos ni mayúsculas ("Práctico", "practico" y "PRACTICO" son lo mismo).
    private static String normalizar(String texto) {
        if (texto == null) {
            return "";
        }

        return java.text.Normalizer
                .normalize(texto.trim(), java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .toLowerCase();
    }

    public List<GrupoEstudioDTO.MiSolicitudResponse> listarSolicitudesDeEstudiante(Long idEstudiante) {
        // Las aceptadas no se listan: al aceptarlas se borran, y si quedó alguna de antes
        // tampoco debe aparecer (el estudiante ya está en el grupo).
        return solicitudRepository.findByEstudiante_IdUsuarioOrderByFechaSolicitudDesc(idEstudiante).stream()
                .filter(solicitud -> !"ACEPTADA".equals(solicitud.getEstado()))
                .map(solicitud -> new GrupoEstudioDTO.MiSolicitudResponse(
                        solicitud.getIdSolicitud(),
                        solicitud.getGrupo().getIdGrupo(),
                        solicitud.getGrupo().getNombre(),
                        solicitud.getEstado(),
                        solicitud.getFechaSolicitud()))
                .collect(Collectors.toList());
    }

    private GrupoEstudioDTO.Response toResponse(GrupoEstudio grupo, String mensaje) {
        int actuales = (grupo.getParticipantes() != null) ? grupo.getParticipantes().size() : 0;
        return new GrupoEstudioDTO.Response(
                grupo.getIdGrupo(),
                grupo.getNombre(),
                grupo.getDescripcion(),
                grupo.getMetodoEstudio(),
                grupo.getCapacidadMaxima(),
                actuales,
                grupo.getEstado(),
                grupo.getTipoGrupo(),
                grupo.getCreador().getIdUsuario(),
                mensaje
        );
    }

    private GrupoEstudioDTO.SolicitudResponse toSolicitudResponse(SolicitudGrupo solicitud, String mensaje) {
        return new GrupoEstudioDTO.SolicitudResponse(
                solicitud.getIdSolicitud(),
                solicitud.getGrupo().getIdGrupo(),
                solicitud.getEstado(),
                mensaje
        );
    }
}
