package com.yachaniapi.sesion.service;

import com.yachaniapi.sesion.entity.NotificacionSesion;
import com.yachaniapi.sesion.entity.SesionEstudio;
import com.yachaniapi.sesion.repository.NotificacionSesionRepository;
import com.yachaniapi.usuario.entity.Estudiante;
import org.springframework.stereotype.Service;

import com.yachaniapi.sesion.dto.NotificacionResponse;
import com.yachaniapi.sesion.mapper.SesionEstudioMapper;
import com.yachaniapi.usuario.entity.Usuario;
import com.yachaniapi.usuario.exception.UsuarioNoEncontradoException;
import com.yachaniapi.usuario.exception.UsuarioNoEsEstudianteException;
import com.yachaniapi.usuario.repository.UsuarioRepository;
import jakarta.transaction.Transactional;

import java.util.List;

@Service
public class NotificacionSesionService {

    public static final String TIPO_RECORDATORIO = "RECORDATORIO";
    public static final String TIPO_CAMBIO = "CAMBIO";
    private final NotificacionSesionRepository notificacionRepository;
    private final UsuarioRepository usuarioRepository;
    private final SesionEstudioMapper sesionMapper;

    public NotificacionSesionService(
            NotificacionSesionRepository notificacionRepository,
            UsuarioRepository usuarioRepository,
            SesionEstudioMapper sesionMapper) {

        this.notificacionRepository = notificacionRepository;
        this.usuarioRepository = usuarioRepository;
        this.sesionMapper = sesionMapper;
    }

    public void enviarRecordatorio(SesionEstudio sesion) {
        guardarParaTodos(sesion, TIPO_RECORDATORIO);
    }

    public void notificarCambio(SesionEstudio sesion) {
        guardarParaTodos(sesion, TIPO_CAMBIO);
    }

    public void eliminarRecordatorios(SesionEstudio sesion) {
        notificacionRepository.deleteBySesion_IdSesionAndTipo(sesion.getIdSesion(), TIPO_RECORDATORIO);
    }

    @Transactional
    public List<NotificacionResponse> listarNotificaciones(Long idEstudiante) {

        Usuario usuario = usuarioRepository.findById(idEstudiante)
                .orElseThrow(() -> new UsuarioNoEncontradoException("Estudiante no encontrado"));

        if (!(usuario instanceof com.yachaniapi.usuario.entity.Estudiante)) {
            throw new UsuarioNoEsEstudianteException("Solo los estudiantes reciben notificaciones de sesiones");
        }

        return notificacionRepository
                .findByEstudiante_IdUsuarioAndSesion_EstadoOrderByFechaCreacionDesc(
                        idEstudiante, SesionEstudioService.ESTADO_PROGRAMADA)
                .stream()
                .map(sesionMapper::toNotificacionResponse)
                .toList();
    }

    private void guardarParaTodos(SesionEstudio sesion, String tipo) {

        for (Estudiante estudiante : sesion.getGrupo().getParticipantes()) {

            NotificacionSesion notificacion = new NotificacionSesion();
            notificacion.setSesion(sesion);
            notificacion.setEstudiante(estudiante);
            notificacion.setTipo(tipo);

            notificacionRepository.save(notificacion);
        }
    }
}