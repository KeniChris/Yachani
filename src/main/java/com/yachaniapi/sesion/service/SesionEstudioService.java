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
import jakarta.transaction.Transactional;
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

    public SesionEstudioService(
            SesionEstudioRepository sesionRepository,
            GrupoEstudioRepository grupoEstudioRepository,
            SesionEstudioMapper sesionMapper,
            NotificacionSesionService notificacionService) {

        this.sesionRepository = sesionRepository;
        this.grupoEstudioRepository = grupoEstudioRepository;
        this.sesionMapper = sesionMapper;
        this.notificacionService = notificacionService;
    }

    @Transactional
    public SesionResponse crearSesion(Long idGrupo, SesionRequest request) {

        GrupoEstudio grupo = buscarGrupo(idGrupo);
        validarTutorDelGrupo(grupo, request.getIdTutor());

        SesionEstudio sesion = new SesionEstudio();
        sesion.setGrupo(grupo);
        sesion.setTema(request.getTema().trim());
        sesion.setDescripcion(request.getDescripcion().trim());
        sesion.setFechaHoraInicio(request.getFechaHoraInicio());
        sesion.setDuracionMinutos(request.getDuracionMinutos());
        sesion.setLugar(request.getLugar().trim());
        sesion.setEstado(ESTADO_PROGRAMADA);
        sesion.setRecordatorioEnviado(false);

        SesionEstudio sesionGuardada = sesionRepository.save(sesion);

        return sesionMapper.toSesionResponse(sesionGuardada, "Se creó correctamente la sesión");
    }

    @Transactional
    public List<SesionResponse> listarSesiones(Long idGrupo) {

        buscarGrupo(idGrupo);

        return sesionRepository
                .findByGrupo_IdGrupoAndEstadoOrderByFechaHoraInicioAsc(idGrupo, ESTADO_PROGRAMADA)
                .stream()
                .map(sesion -> sesionMapper.toSesionResponse(sesion, null))
                .toList();
    }

    @Transactional
    public SesionResponse modificarSesion(Long idGrupo, Long idSesion, SesionRequest request) {

        GrupoEstudio grupo = buscarGrupo(idGrupo);
        validarTutorDelGrupo(grupo, request.getIdTutor());

        SesionEstudio sesion = buscarSesion(idSesion, idGrupo);

        if (ESTADO_CANCELADA.equals(sesion.getEstado())) {
            throw new SesionCanceladaException("No se puede modificar una sesión cancelada");
        }

        boolean cambioFecha = !sesion.getFechaHoraInicio().equals(request.getFechaHoraInicio());

        sesion.setTema(request.getTema().trim());
        sesion.setDescripcion(request.getDescripcion().trim());
        sesion.setFechaHoraInicio(request.getFechaHoraInicio());
        sesion.setDuracionMinutos(request.getDuracionMinutos());
        sesion.setLugar(request.getLugar().trim());

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

        GrupoEstudio grupo = buscarGrupo(idGrupo);
        validarTutorDelGrupo(grupo, idTutor);

        SesionEstudio sesion = buscarSesion(idSesion, idGrupo);

        if (ESTADO_CANCELADA.equals(sesion.getEstado())) {
            throw new SesionCanceladaException("La sesión ya fue cancelada");
        }

        sesion.setEstado(ESTADO_CANCELADA);
        notificacionService.eliminarRecordatorios(sesion);
        SesionEstudio sesionCancelada = sesionRepository.save(sesion);

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
        if (!grupo.getCreador().getIdUsuario().equals(idTutor)) {
            throw new TutorNoAutorizadoException("Solo el tutor del grupo puede organizar sesiones");
        }
    }
}