package com.yachaniapi.notificacion.service;

import com.yachaniapi.grupoestudio.entity.SolicitudGrupo;
import com.yachaniapi.grupoestudio.dto.response.PaginaResponse;
import com.yachaniapi.notificacion.dto.NotificacionResponse;
import com.yachaniapi.notificacion.entity.Notificacion;
import com.yachaniapi.notificacion.exception.NotificacionExceptions.NotificacionNoEncontradaException;
import com.yachaniapi.notificacion.mapper.NotificacionMapper;
import com.yachaniapi.notificacion.repository.NotificacionRepository;
import com.yachaniapi.sesion.entity.SesionEstudio;
import com.yachaniapi.usuario.entity.Usuario;
import com.yachaniapi.usuario.service.UsuarioActualService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.LinkedHashMap;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificacionService {
    private final NotificacionRepository notificacionRepository;
    private final UsuarioActualService usuarioActual;
    private final NotificacionMapper notificacionMapper;

    @Transactional
    public void notificarSolicitudRecibida(SolicitudGrupo solicitud) {
        crearSolicitud(solicitud.getGrupo().getCreador(), solicitud, "SOLICITUD_RECIBIDA",
                solicitud.getEstudiante().getNombres() + " " + solicitud.getEstudiante().getApellidos()
                        + " solicitó unirse al grupo " + solicitud.getGrupo().getNombre());
    }

    @Transactional
    public void notificarSolicitudAceptada(SolicitudGrupo solicitud) {
        crearSolicitud(solicitud.getEstudiante(), solicitud, "SOLICITUD_ACEPTADA",
                "Tu solicitud para unirte al grupo " + solicitud.getGrupo().getNombre() + " fue aceptada");
    }

    @Transactional
    public void notificarSolicitudRechazada(SolicitudGrupo solicitud) {
        crearSolicitud(solicitud.getEstudiante(), solicitud, "SOLICITUD_RECHAZADA",
                "Tu solicitud para unirte al grupo " + solicitud.getGrupo().getNombre() + " fue rechazada");
    }

    public PaginaResponse<NotificacionResponse> listar(Long idEnviado, Pageable pageable) {
        Long id = usuarioActual.exigirPropio(idEnviado).getIdUsuario();
        if (pageable.isUnpaged() || pageable.getPageSize() > 100) {
            throw new IllegalArgumentException("Usa páginas de entre 1 y 100 registros");
        }
        var permitidos = Set.of("fechaCreacion", "idNotificacion");
        pageable.getSort().forEach(orden -> {
            if (!permitidos.contains(orden.getProperty())) {
                throw new IllegalArgumentException("Campo de ordenamiento no permitido");
            }
        });
        return PaginaResponse.desde(notificacionRepository.findByDestinatario_IdUsuario(id, pageable)
                .map(notificacionMapper::toNotificacionResponse));
    }

    public long contarNoLeidas() {
        return notificacionRepository.countByDestinatario_IdUsuarioAndLeidaFalse(usuarioActual.idUsuario());
    }

    @Transactional
    public NotificacionResponse marcarComoLeida(Long idNotificacion, Long idEnviado) {
        Long id = usuarioActual.exigirPropio(idEnviado).getIdUsuario();
        Notificacion notificacion = notificacionRepository
                .findByIdNotificacionAndDestinatario_IdUsuario(idNotificacion, id)
                .orElseThrow(() -> new NotificacionNoEncontradaException("La notificación no existe"));
        notificacion.setLeida(true);
        return notificacionMapper.toNotificacionResponse(notificacionRepository.save(notificacion));
    }

    @Transactional
    public int marcarTodasLeidas() {
        return notificacionRepository.marcarTodasLeidas(usuarioActual.idUsuario());
    }

    @Transactional
    public void notificarSesion(SesionEstudio sesion, String tipo) {
        var destinatarios = new LinkedHashMap<Long, Usuario>();
        var grupo = sesion.getGrupo();
        destinatarios.put(grupo.getCreador().getIdUsuario(), grupo.getCreador());
        grupo.getParticipantes().forEach(p -> destinatarios.putIfAbsent(p.getIdUsuario(), p));
        String mensaje = switch (tipo) {
            case "SESION_RECORDATORIO" -> "La sesión " + sesion.getTema() + " empieza el " + sesion.getFechaHoraInicio();
            case "SESION_CANCELADA" -> "Se canceló la sesión " + sesion.getTema();
            default -> "Se modificó la sesión " + sesion.getTema();
        };
        for (Usuario destinatario : destinatarios.values()) {
            Notificacion n = new Notificacion();
            n.setDestinatario(destinatario);
            n.setGrupo(grupo);
            n.setSesion(sesion);
            n.setTipo(tipo);
            n.setMensaje(mensaje);
            notificacionRepository.save(n);
        }
    }

    @Transactional
    public void eliminarRecordatoriosPendientes(SesionEstudio sesion) {
        notificacionRepository.deleteBySesion_IdSesionAndTipoAndLeidaFalse(
                sesion.getIdSesion(), "SESION_RECORDATORIO");
    }

    private void crearSolicitud(Usuario destinatario, SolicitudGrupo solicitud, String tipo, String mensaje) {
        Notificacion n = new Notificacion();
        n.setDestinatario(destinatario);
        n.setSolicitud(solicitud);
        n.setGrupo(solicitud.getGrupo());
        n.setTipo(tipo);
        n.setMensaje(mensaje);
        notificacionRepository.save(n);
    }
}
