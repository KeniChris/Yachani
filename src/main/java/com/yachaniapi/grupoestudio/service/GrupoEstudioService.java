package com.yachaniapi.grupoestudio.service;

import com.yachaniapi.grupoestudio.dto.GrupoEstudioDTO;
import com.yachaniapi.grupoestudio.entity.GrupoEstudio;
import com.yachaniapi.grupoestudio.entity.SolicitudGrupo;
import com.yachaniapi.grupoestudio.exception.GrupoEstudioExceptions.*;
import com.yachaniapi.grupoestudio.repository.GrupoEstudioRepository;
import com.yachaniapi.grupoestudio.repository.SolicitudGrupoRepository;
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

    private final GrupoEstudioRepository grupoRepository;
    private final SolicitudGrupoRepository solicitudRepository;
    private final UsuarioRepository usuarioRepository;

    public GrupoEstudioService(
            GrupoEstudioRepository grupoRepository,
            SolicitudGrupoRepository solicitudRepository,
            UsuarioRepository usuarioRepository) {
        this.grupoRepository = grupoRepository;
        this.solicitudRepository = solicitudRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public GrupoEstudioDTO.Response crearGrupo(GrupoEstudioDTO.CrearRequest request) {
        Usuario usuario = usuarioRepository.findById(request.getIdTutor())
                .orElseThrow(() -> new UsuarioNoEncontradoException("Tutor no encontrado"));

        if (!(usuario instanceof Tutor tutor)) {
            throw new IllegalArgumentException("El usuario debe ser un Tutor para crear grupos");
        }

        GrupoEstudio grupo = new GrupoEstudio();
        grupo.setNombre(request.getNombre().trim());
        grupo.setDescripcion(request.getDescripcion().trim());
        grupo.setMetodoEstudio(request.getMetodoEstudio().trim());
        grupo.setCapacidadMaxima(request.getCapacidadMaxima());
        grupo.setEstado("Activo");
        grupo.setCreador(tutor);

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

        int actuales = (grupo.getParticipantes() != null) ? grupo.getParticipantes().size() : 0;
        if (actuales >= grupo.getCapacidadMaxima()) {
            throw new GrupoLlenoException("El grupo ya alcanzó su capacidad máxima");
        }

        if (grupo.getParticipantes().contains(estudiante)) {
            throw new IllegalArgumentException("El estudiante ya es miembro del grupo");
        }

        if (solicitudRepository.existsByGrupo_IdGrupoAndEstudiante_IdUsuario(idGrupo, estudiante.getIdUsuario())) {
            throw new IllegalArgumentException("El estudiante ya envió una solicitud a este grupo");
        }

        SolicitudGrupo solicitud = new SolicitudGrupo();
        solicitud.setGrupo(grupo);
        solicitud.setEstudiante(estudiante);
        solicitud.setEstado("PENDIENTE");

        SolicitudGrupo guardada = solicitudRepository.save(solicitud);
        System.out.println("Enviando notificación: Solicitud de " + estudiante.getNombres() + " al grupo " + grupo.getNombre());
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
        solicitudRepository.save(solicitud);
        grupo.getParticipantes().add(solicitud.getEstudiante());
        grupoRepository.save(grupo);
        System.out.println("Enviando notificación: Solicitud aceptada para el grupo " + grupo.getNombre());
        return toSolicitudResponse(solicitud, "Solicitud aceptada");
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
        System.out.println("Enviando notificación: Solicitud rechazada para el grupo " + solicitud.getGrupo().getNombre());
        return toSolicitudResponse(solicitud, "Solicitud rechazada");
    }

    public List<GrupoEstudioDTO.Response> buscarGrupos(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new IllegalArgumentException("Ingrese un curso o tema");
        }
        return grupoRepository.findByKeyword(keyword.trim()).stream()
                .map(g -> toResponse(g, "Búsqueda completada"))
                .collect(Collectors.toList());
    }

    public List<GrupoEstudioDTO.Response> filtrarGrupos(String curso, String metodoEstudio, Integer cantidadIntegrantes) {
        return grupoRepository.findByFiltros(curso, metodoEstudio, cantidadIntegrantes).stream()
                .map(g -> toResponse(g, "Filtrado completado"))
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
