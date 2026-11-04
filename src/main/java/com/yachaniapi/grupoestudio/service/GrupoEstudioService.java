package com.yachaniapi.grupoestudio.service;

import com.yachaniapi.grupoestudio.dto.request.*;
import com.yachaniapi.grupoestudio.dto.response.*;
import com.yachaniapi.grupoestudio.entity.GrupoEstudio;
import com.yachaniapi.grupoestudio.entity.SolicitudGrupo;
import com.yachaniapi.grupoestudio.entity.TipoGrupo;
import com.yachaniapi.grupoestudio.exception.ConflictoGrupoException;
import com.yachaniapi.grupoestudio.exception.GrupoEstudioExceptions.*;
import com.yachaniapi.grupoestudio.mapper.GrupoEstudioMapper;
import com.yachaniapi.grupoestudio.repository.GrupoEstudioRepository;
import com.yachaniapi.grupoestudio.repository.SolicitudGrupoRepository;
import com.yachaniapi.notificacion.service.NotificacionService;
import com.yachaniapi.usuario.entity.Estudiante;
import com.yachaniapi.usuario.entity.Tutor;
import com.yachaniapi.usuario.entity.Usuario;
import com.yachaniapi.usuario.exception.UsuarioNoEncontradoException;
import com.yachaniapi.usuario.exception.UsuarioNoEsEstudianteException;
import com.yachaniapi.usuario.repository.UsuarioRepository;
import jakarta.persistence.criteria.JoinType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GrupoEstudioService {

    private static final int MAX_GRUPOS_TUTOR = 10;
    private static final int MAX_GRUPOS_ESTUDIANTE = 5;
    private static final Set<String> ORDEN_GRUPOS = Set.of(
            "idGrupo", "nombre", "metodoEstudio", "capacidadMaxima", "tipoGrupo"
    );
    private static final Set<String> ORDEN_SOLICITUDES = Set.of(
            "idSolicitud", "fechaSolicitud"
    );

    private final GrupoEstudioRepository grupoRepository;
    private final SolicitudGrupoRepository solicitudRepository;
    private final UsuarioRepository usuarioRepository;
    private final NotificacionService notificacionService;
    private final GrupoEstudioMapper mapper;
    private final ReglasGrupoService reglas;
    private final com.yachaniapi.usuario.service.UsuarioActualService usuarioActual;
    private final AccesoGrupoService acceso;

    @Transactional
    public GrupoResponse crearGrupo(CrearGrupoRequest request) {
        Usuario usuario = usuarioActual.exigirPropio(request.idCreador());
        grupoRepository.bloquearCreador(usuario.getIdUsuario())
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado"));
        TipoGrupo tipo;
        int maximo;
        if (usuario instanceof Tutor) {
            tipo = TipoGrupo.TUTOR;
            maximo = MAX_GRUPOS_TUTOR;
        } else if (usuario instanceof Estudiante) {
            tipo = TipoGrupo.ESTUDIANTES;
            maximo = MAX_GRUPOS_ESTUDIANTE;
        } else {
            throw new IllegalArgumentException("Solo tutores o estudiantes pueden crear grupos");
        }
        if (grupoRepository.countByCreador_IdUsuario(usuario.getIdUsuario()) >= maximo) {
            throw new ConflictoGrupoException("Alcanzaste el máximo de " + maximo + " grupos");
        }
        validarCapacidad(request.capacidadMaxima());
        GrupoEstudio grupo = mapper.toEntity(request);
        grupo.setNombre(request.nombre().strip());
        grupo.setDescripcion(request.descripcion().strip());
        grupo.setMetodoEstudio(request.metodoEstudio().strip());
        grupo.setCreador(usuario);
        grupo.setTipoGrupo(tipo);
        grupo.setEstado("ACTIVO");
        grupo.setPermitirPublicaciones(true);
        if (usuario instanceof Estudiante estudiante) {
            grupo.getParticipantes().add(estudiante);
        }
        return respuesta(grupoRepository.save(grupo), "Grupo creado correctamente");
    }

    public PaginaResponse<GrupoResponse> listarGrupos(
            Long idUsuario, String query, String curso, String metodoEstudio,
            Integer cantidadIntegrantes, TipoGrupo tipoGrupo, Pageable pageable) {
        validarPaginacion(pageable, ORDEN_GRUPOS);
        if (idUsuario != null) usuarioActual.exigirPropio(idUsuario);
        if (cantidadIntegrantes != null && cantidadIntegrantes < 0) {
            throw new IllegalArgumentException("La cantidad de integrantes no puede ser negativa");
        }
        Specification<GrupoEstudio> filtros = (root, consulta, cb) ->
                cb.equal(root.get("estado"), "ACTIVO");
        if (idUsuario != null) {
            filtros = filtros.and((root, consulta, cb) -> {
                consulta.distinct(true);
                var participante = root.join("participantes", JoinType.LEFT);
                return cb.or(
                        cb.equal(root.get("creador").get("idUsuario"), idUsuario),
                        cb.equal(participante.get("idUsuario"), idUsuario)
                );
            });
        }
        for (String texto : new String[]{query, curso}) {
            if (texto != null && !texto.isBlank()) {
                String patron = "%" + escaparLike(texto.strip().toLowerCase(Locale.ROOT)) + "%";
                filtros = filtros.and((root, consulta, cb) -> cb.or(
                        cb.like(cb.lower(root.get("nombre")), patron, '\\'),
                        cb.like(cb.lower(root.get("descripcion")), patron, '\\')
                ));
            }
        }
        if (metodoEstudio != null && !metodoEstudio.isBlank()) {
            String metodo = metodoEstudio.strip().toLowerCase(Locale.ROOT);
            filtros = filtros.and((root, consulta, cb) ->
                    cb.equal(cb.lower(root.get("metodoEstudio")), metodo));
        }
        if (cantidadIntegrantes != null) {
            filtros = filtros.and((root, consulta, cb) ->
                    cb.equal(cb.size(root.get("participantes")), cantidadIntegrantes));
        }
        if (tipoGrupo != null) {
            filtros = filtros.and((root, consulta, cb) -> cb.equal(root.get("tipoGrupo"), tipoGrupo));
        }
        return PaginaResponse.desde(grupoRepository.findAll(filtros, pageable)
                .map(grupo -> respuesta(grupo, null)));
    }

    public GrupoResponse obtenerGrupo(Long idGrupo) {
        return respuesta(buscarGrupo(idGrupo), null);
    }

    @Transactional
    public GrupoResponse editarNombre(Long idGrupo, EditarNombreGrupoRequest request) {
        GrupoEstudio grupo = acceso.administrador(idGrupo, true);
        grupo.setNombre(request.nombre().strip());
        return respuesta(grupoRepository.save(grupo), "Nombre actualizado");
    }

    @Transactional
    public GrupoResponse actualizarCapacidad(Long idGrupo, EditarCapacidadGrupoRequest request) {
        GrupoEstudio grupo = acceso.administrador(idGrupo, true);
        validarCapacidad(request.nuevaCapacidad());
        int actuales = reglas.integrantesActuales(grupo);
        if (request.nuevaCapacidad() < actuales) {
            throw new ConflictoGrupoException(
                    "La capacidad no puede ser menor a los " + actuales + " estudiantes inscritos");
        }
        grupo.setCapacidadMaxima(request.nuevaCapacidad());
        return respuesta(grupoRepository.save(grupo), "Capacidad actualizada");
    }

    @Transactional
    public GrupoResponse cambiarPermitirPublicaciones(
            Long idGrupo, EditarPublicacionesGrupoRequest request) {
        GrupoEstudio grupo = acceso.administrador(idGrupo, true);
        if (grupo.getTipoGrupo() != TipoGrupo.TUTOR) {
            throw new IllegalArgumentException(
                    "Los grupos entre estudiantes siempre permiten publicar recursos");
        }
        if (request.permitirPublicaciones() == null) {
            throw new IllegalArgumentException("Indica si se permiten publicaciones");
        }
        grupo.setPermitirPublicaciones(request.permitirPublicaciones());
        return respuesta(grupoRepository.save(grupo), "Configuración actualizada");
    }

    public List<IntegranteGrupoResponse> listarIntegrantes(Long idGrupo) {
        GrupoEstudio grupo = acceso.miembro(idGrupo, false);
        Map<Long, Usuario> integrantes = new LinkedHashMap<>();
        integrantes.put(grupo.getCreador().getIdUsuario(), grupo.getCreador());
        grupo.getParticipantes().forEach(p -> integrantes.putIfAbsent(p.getIdUsuario(), p));
        return integrantes.values().stream().map(usuario -> {
            boolean administrador = reglas.esAdministrador(grupo, usuario.getIdUsuario());
            String tipo = administrador && grupo.getTipoGrupo() == TipoGrupo.TUTOR
                    ? "TUTOR" : "ESTUDIANTE";
            return mapper.toIntegrante(usuario, nombreCompleto(usuario), tipo, administrador);
        }).toList();
    }

    @Transactional
    public void eliminarIntegrante(Long idGrupo, Long idEstudiante) {
        GrupoEstudio grupo = acceso.administrador(idGrupo, true);
        if (reglas.esAdministrador(grupo, idEstudiante)) {
            throw new ConflictoGrupoException("No se puede eliminar al creador del grupo");
        }
        boolean eliminado = grupo.getParticipantes()
                .removeIf(p -> p.getIdUsuario().equals(idEstudiante));
        if (!eliminado) {
            throw new IntegranteNoEncontradoException("El estudiante no pertenece a este grupo");
        }
        grupoRepository.save(grupo);
    }

    @Transactional
    public SolicitudGrupoResponse solicitarUnirse(Long idGrupo, SolicitudGrupoRequest request) {
        GrupoEstudio grupo = bloquearGrupo(idGrupo);
        usuarioActual.exigirPropio(request.idEstudiante());
        Estudiante estudiante = usuarioActual.estudianteActual();
        if (reglas.esIntegrante(grupo, estudiante.getIdUsuario())) {
            throw new ConflictoGrupoException("Ya perteneces a este grupo");
        }
        if (solicitudRepository.existsByGrupo_IdGrupoAndEstudiante_IdUsuario(
                idGrupo, estudiante.getIdUsuario())) {
            throw new ConflictoGrupoException("Ya existe una solicitud de este estudiante para el grupo");
        }
        if (reglas.cuposDisponibles(grupo) == 0) {
            throw new GrupoLlenoException("El grupo ya alcanzó su capacidad máxima");
        }
        SolicitudGrupo solicitud = new SolicitudGrupo();
        solicitud.setGrupo(grupo);
        solicitud.setEstudiante(estudiante);
        solicitud.setEstado("PENDIENTE");
        SolicitudGrupo guardada = solicitudRepository.save(solicitud);
        notificacionService.notificarSolicitudRecibida(guardada);
        return mapper.toSolicitud(guardada, "Solicitud enviada correctamente");
    }

    @Transactional
    public SolicitudGrupoResponse aceptarSolicitud(Long idSolicitud) {
        GrupoEstudio grupo = bloquearGrupoDeSolicitud(idSolicitud);
        SolicitudGrupo solicitud = buscarSolicitudPendiente(idSolicitud);
        if (reglas.esIntegrante(grupo, solicitud.getEstudiante().getIdUsuario())) {
            throw new ConflictoGrupoException("El estudiante ya pertenece al grupo");
        }
        if (reglas.cuposDisponibles(grupo) == 0) {
            throw new GrupoLlenoException("El grupo ya alcanzó su capacidad máxima");
        }
        grupo.getParticipantes().add(solicitud.getEstudiante());
        solicitud.setEstado("ACEPTADA");
        grupoRepository.save(grupo);
        solicitudRepository.save(solicitud);
        notificacionService.notificarSolicitudAceptada(solicitud);
        return mapper.toSolicitud(solicitud, "Solicitud aceptada");
    }

    @Transactional
    public SolicitudGrupoResponse rechazarSolicitud(Long idSolicitud) {
        bloquearGrupoDeSolicitud(idSolicitud);
        SolicitudGrupo solicitud = buscarSolicitudPendiente(idSolicitud);
        solicitud.setEstado("RECHAZADA");
        solicitudRepository.save(solicitud);
        notificacionService.notificarSolicitudRechazada(solicitud);
        return mapper.toSolicitud(solicitud, "Solicitud rechazada");
    }

    public PaginaResponse<SolicitudPendienteResponse> listarSolicitudesPendientes(
            Long idGrupo, Pageable pageable) {
        validarPaginacion(pageable, ORDEN_SOLICITUDES);
        acceso.administrador(idGrupo, false);
        return PaginaResponse.desde(solicitudRepository
                .findByGrupo_IdGrupoAndEstado(idGrupo, "PENDIENTE", pageable)
                .map(s -> mapper.toPendiente(s, nombreCompleto(s.getEstudiante()))));
    }

    public PaginaResponse<SolicitudPropiaResponse> listarMisSolicitudes(
            Long idEstudiante, Pageable pageable) {
        validarPaginacion(pageable, ORDEN_SOLICITUDES);
        idEstudiante = usuarioActual.exigirPropio(idEstudiante).getIdUsuario();
        if (!(buscarUsuario(idEstudiante) instanceof Estudiante)) {
            throw new UsuarioNoEsEstudianteException("Solo los estudiantes tienen solicitudes de ingreso");
        }
        return PaginaResponse.desde(solicitudRepository
                .findByEstudiante_IdUsuarioAndEstado(idEstudiante, "PENDIENTE", pageable)
                .map(mapper::toPropia));
    }

    private Usuario buscarUsuario(Long idUsuario) {
        return usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado"));
    }

    private GrupoEstudio buscarGrupo(Long idGrupo) {
        return grupoRepository.findById(idGrupo)
                .orElseThrow(() -> new GrupoNoEncontradoException("El grupo de estudio no existe"));
    }

    private GrupoEstudio bloquearGrupo(Long idGrupo) {
        GrupoEstudio grupo = grupoRepository.buscarConBloqueo(idGrupo)
                .orElseThrow(() -> new GrupoNoEncontradoException("El grupo de estudio no existe"));
        if (!"ACTIVO".equals(grupo.getEstado())) {
            throw new ConflictoGrupoException("El grupo no está activo");
        }
        return grupo;
    }

    private GrupoEstudio bloquearGrupoDeSolicitud(Long idSolicitud) {
        Long idGrupo = solicitudRepository.buscarIdGrupo(idSolicitud)
                .orElseThrow(() -> new SolicitudNoEncontradaException("La solicitud no existe"));
        return acceso.administrador(idGrupo, true);
    }

    private SolicitudGrupo buscarSolicitudPendiente(Long idSolicitud) {
        SolicitudGrupo solicitud = solicitudRepository.findById(idSolicitud)
                .orElseThrow(() -> new SolicitudNoEncontradaException("La solicitud no existe"));
        if (!"PENDIENTE".equals(solicitud.getEstado())) {
            throw new ConflictoGrupoException("La solicitud ya fue procesada");
        }
        return solicitud;
    }

    private GrupoResponse respuesta(GrupoEstudio grupo, String mensaje) {
        return mapper.toResponse(
                grupo, reglas.integrantesActuales(grupo), reglas.cuposDisponibles(grupo),
                reglas.publicacionesAbiertas(grupo), nombreCompleto(grupo.getCreador()),
                grupo.getTipoGrupo() == TipoGrupo.TUTOR ? "TUTOR" : "ESTUDIANTE", mensaje
        );
    }

    private void validarCapacidad(Integer capacidad) {
        if (capacidad == null || capacidad < 2) {
            throw new IllegalArgumentException("La capacidad mínima es de 2 estudiantes");
        }
    }

    private void validarPaginacion(Pageable pageable, Set<String> campos) {
        if (pageable == null || pageable.isUnpaged() || pageable.getPageSize() > 100) {
            throw new IllegalArgumentException("Usa páginas de entre 1 y 100 registros");
        }
        pageable.getSort().forEach(orden -> {
            if (!campos.contains(orden.getProperty())) {
                throw new IllegalArgumentException("Campo de ordenamiento no permitido: " + orden.getProperty());
            }
        });
    }

    private String nombreCompleto(Usuario usuario) {
        return usuario.getNombres() + " " + usuario.getApellidos();
    }

    private String escaparLike(String texto) {
        return texto.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
    }
}
