package com.yachaniapi.sesion.service;

import com.yachaniapi.grupoestudio.entity.GrupoEstudio;
import com.yachaniapi.grupoestudio.exception.GrupoEstudioExceptions.GrupoNoEncontradoException;
import com.yachaniapi.grupoestudio.repository.GrupoEstudioRepository;
import com.yachaniapi.sesion.dto.SesionRequest;
import com.yachaniapi.sesion.dto.SesionResponse;
import com.yachaniapi.sesion.entity.SesionEstudio;
import com.yachaniapi.sesion.exception.SesionExceptions.*;
import com.yachaniapi.sesion.mapper.SesionEstudioMapper;
import com.yachaniapi.sesion.repository.SesionEstudioRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SesionEstudioService {

    public static final String ESTADO_PROGRAMADA = "PROGRAMADA";
    public static final String ESTADO_CANCELADA = "CANCELADA";

    private final SesionEstudioRepository sesionRepository;
    private final GrupoEstudioRepository grupoEstudioRepository;
    private final SesionEstudioMapper sesionMapper;
    private final NotificacionSesionService notificacionService;
    private final com.yachaniapi.usuario.service.UsuarioActualService usuarioActual;
    private final com.yachaniapi.grupoestudio.service.AccesoGrupoService acceso;
    private final java.time.Clock clock;

    public SesionEstudioService(
            SesionEstudioRepository sesionRepository,
            GrupoEstudioRepository grupoEstudioRepository,
            SesionEstudioMapper sesionMapper,
            NotificacionSesionService notificacionService,
            com.yachaniapi.usuario.service.UsuarioActualService usuarioActual,
            com.yachaniapi.grupoestudio.service.AccesoGrupoService acceso,
            java.time.Clock clock) {
        this.usuarioActual = usuarioActual;
        this.acceso = acceso;
        this.clock = clock;

        this.sesionRepository = sesionRepository;
        this.grupoEstudioRepository = grupoEstudioRepository;
        this.sesionMapper = sesionMapper;
        this.notificacionService = notificacionService;
    }

    @Transactional
    public SesionResponse crearSesion(Long idGrupo, SesionRequest request) {

        GrupoEstudio grupo = acceso.administrador(idGrupo, true);
        validarTutorDelGrupo(grupo, request.getIdTutor());

        SesionEstudio sesion = new SesionEstudio();
        sesion.setGrupo(grupo);
        validarFecha(request.getFechaHoraInicio());
        sesion.setTema(request.getTema().trim());
        sesion.setDescripcion(limpiar(request.getDescripcion()));
        sesion.setFechaHoraInicio(request.getFechaHoraInicio());
        sesion.setDuracionMinutos(request.getDuracionMinutos());
        sesion.setLugar(limpiar(request.getLugar()));
        sesion.setEstado(ESTADO_PROGRAMADA);
        sesion.setRecordatorioEnviado(false);

        SesionEstudio sesionGuardada = sesionRepository.save(sesion);

        return sesionMapper.toSesionResponse(sesionGuardada, "Se creó correctamente la sesión");
    }

    @Transactional
    public List<SesionResponse> listarSesiones(Long idGrupo) {

        acceso.miembro(idGrupo, false);

        return sesionRepository
                .findByGrupo_IdGrupoAndEstadoOrderByFechaHoraInicioAsc(idGrupo, ESTADO_PROGRAMADA)
                .stream()
                .map(sesion -> sesionMapper.toSesionResponse(sesion, null))
                .toList();
    }

    @Transactional
    public SesionResponse modificarSesion(Long idGrupo, Long idSesion, SesionRequest request) {

        GrupoEstudio grupo = acceso.administrador(idGrupo, true);
        validarTutorDelGrupo(grupo, request.getIdTutor());

        SesionEstudio sesion = buscarSesion(idSesion, idGrupo);

        if (ESTADO_CANCELADA.equals(sesion.getEstado())) {
            throw new SesionCanceladaException("No se puede modificar una sesión cancelada");
        }

        boolean cambioFecha = !sesion.getFechaHoraInicio().equals(request.getFechaHoraInicio());

        validarFecha(request.getFechaHoraInicio());
        sesion.setTema(request.getTema().trim());
        sesion.setDescripcion(limpiar(request.getDescripcion()));
        sesion.setFechaHoraInicio(request.getFechaHoraInicio());
        sesion.setDuracionMinutos(request.getDuracionMinutos());
        sesion.setLugar(limpiar(request.getLugar()));

        if (cambioFecha) {
            sesion.setRecordatorioEnviado(false);
            notificacionService.eliminarRecordatorios(sesion);
        }

        SesionEstudio sesionActualizada = sesionRepository.save(sesion);
        notificacionService.notificarCambio(sesionActualizada);
        return sesionMapper.toSesionResponse(sesionActualizada, "Se modificó correctamente");
    }

    @Transactional
    public SesionResponse cancelarSesion(Long idGrupo, Long idSesion, Long idTutor) {

        GrupoEstudio grupo = acceso.administrador(idGrupo, true);
        validarTutorDelGrupo(grupo, idTutor);

        SesionEstudio sesion = buscarSesion(idSesion, idGrupo);

        if (ESTADO_CANCELADA.equals(sesion.getEstado())) {
            throw new SesionCanceladaException("La sesión ya fue cancelada");
        }

        sesion.setEstado(ESTADO_CANCELADA);
        notificacionService.eliminarRecordatorios(sesion);
        SesionEstudio sesionCancelada = sesionRepository.save(sesion);
        notificacionService.notificarCancelacion(sesionCancelada);

        return sesionMapper.toSesionResponse(sesionCancelada, "Se canceló correctamente la sesión");
    }

    private GrupoEstudio buscarGrupo(Long idGrupo) {
        return grupoEstudioRepository.findById(idGrupo)
                .orElseThrow(() -> new GrupoNoEncontradoException("El grupo de estudio no existe"));
    }

    private SesionEstudio buscarSesion(Long idSesion, Long idGrupo) {
        return sesionRepository.findByIdSesionAndGrupo_IdGrupo(idSesion, idGrupo)
                .orElseThrow(() -> new SesionNoEncontradaException("La sesión no existe"));
    }

    private void validarTutorDelGrupo(GrupoEstudio grupo, Long idTutor) {
        usuarioActual.exigirPropio(idTutor);
        if (!grupo.getCreador().getIdUsuario().equals(usuarioActual.idUsuario())) {
            throw new TutorNoAutorizadoException("Solo el creador del grupo puede organizar sesiones");
        }
    }

    private String limpiar(String valor) {
        return valor == null ? "" : valor.strip();
    }

    private void validarFecha(java.time.LocalDateTime fecha) {
        if (fecha == null || !fecha.isAfter(java.time.LocalDateTime.now(clock))) {
            throw new IllegalArgumentException("Programa la sesión para una fecha y hora futuras");
        }
    }
}