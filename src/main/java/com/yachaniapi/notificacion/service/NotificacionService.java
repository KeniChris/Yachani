package com.yachaniapi.notificacion.service;

import com.yachaniapi.grupoestudio.entity.SolicitudGrupo;
import com.yachaniapi.notificacion.dto.NotificacionResponse;
import com.yachaniapi.notificacion.entity.Notificacion;
import com.yachaniapi.notificacion.exception.NotificacionExceptions.NotificacionNoEncontradaException;
import com.yachaniapi.notificacion.mapper.NotificacionMapper;
import com.yachaniapi.notificacion.repository.NotificacionRepository;
import com.yachaniapi.usuario.entity.Usuario;
import com.yachaniapi.usuario.exception.UsuarioNoEncontradoException;
import com.yachaniapi.usuario.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificacionService {

    private final NotificacionRepository notificacionRepository;
    private final UsuarioRepository usuarioRepository;
    private final NotificacionMapper notificacionMapper;

    public NotificacionService(
            NotificacionRepository notificacionRepository,
            UsuarioRepository usuarioRepository,
            NotificacionMapper notificacionMapper) {

        this.notificacionRepository = notificacionRepository;
        this.usuarioRepository = usuarioRepository;
        this.notificacionMapper = notificacionMapper;
    }

    public void notificarSolicitudRecibida(SolicitudGrupo solicitud) {
        crear(
                solicitud.getGrupo().getCreador(),
                solicitud,
                "SOLICITUD_RECIBIDA",
                solicitud.getEstudiante().getNombres() + " " + solicitud.getEstudiante().getApellidos()
                        + " solicitó unirse al grupo " + solicitud.getGrupo().getNombre()
        );
    }

    public void notificarSolicitudAceptada(SolicitudGrupo solicitud) {
        crear(
                solicitud.getEstudiante(),
                solicitud,
                "SOLICITUD_ACEPTADA",
                "Tu solicitud para unirte al grupo " + solicitud.getGrupo().getNombre() + " fue aceptada"
        );
    }

    public void notificarSolicitudRechazada(SolicitudGrupo solicitud) {
        crear(
                solicitud.getEstudiante(),
                solicitud,
                "SOLICITUD_RECHAZADA",
                "Tu solicitud para unirte al grupo " + solicitud.getGrupo().getNombre() + " fue rechazada"
        );
    }

    @Transactional
    public List<NotificacionResponse> listarNotificaciones(Long idUsuario) {

        if (!usuarioRepository.existsById(idUsuario)) {
            throw new UsuarioNoEncontradoException("Usuario no encontrado");
        }

        return notificacionRepository.findByDestinatario_IdUsuarioOrderByFechaCreacionDesc(idUsuario)
                .stream()
                .map(notificacionMapper::toNotificacionResponse)
                .toList();
    }

    @Transactional
    public NotificacionResponse marcarComoLeida(Long idNotificacion, Long idUsuario) {

        Notificacion notificacion = notificacionRepository
                .findByIdNotificacionAndDestinatario_IdUsuario(idNotificacion, idUsuario)
                .orElseThrow(() -> new NotificacionNoEncontradaException("La notificación no existe"));

        notificacion.setLeida(true);

        return notificacionMapper.toNotificacionResponse(notificacionRepository.save(notificacion));
    }

    private void crear(Usuario destinatario, SolicitudGrupo solicitud, String tipo, String mensaje) {

        Notificacion notificacion = new Notificacion();
        notificacion.setDestinatario(destinatario);
        notificacion.setSolicitud(solicitud);
        notificacion.setTipo(tipo);
        notificacion.setMensaje(mensaje);

        notificacionRepository.save(notificacion);
    }
}
