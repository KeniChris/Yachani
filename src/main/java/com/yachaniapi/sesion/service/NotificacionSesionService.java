package com.yachaniapi.sesion.service;

import com.yachaniapi.notificacion.repository.NotificacionRepository;
import com.yachaniapi.notificacion.service.NotificacionService;
import com.yachaniapi.sesion.dto.NotificacionResponse;
import com.yachaniapi.sesion.entity.SesionEstudio;
import com.yachaniapi.usuario.service.UsuarioActualService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificacionSesionService {
    public static final String TIPO_RECORDATORIO = "RECORDATORIO";
    public static final String TIPO_CAMBIO = "CAMBIO";
    private final NotificacionService notificaciones;
    private final NotificacionRepository repositorio;
    private final UsuarioActualService usuarioActual;

    public void enviarRecordatorio(SesionEstudio sesion) {
        notificaciones.notificarSesion(sesion, "SESION_RECORDATORIO");
    }

    public void notificarCambio(SesionEstudio sesion) {
        notificaciones.notificarSesion(sesion, "SESION_MODIFICADA");
    }

    public void notificarCancelacion(SesionEstudio sesion) {
        notificaciones.notificarSesion(sesion, "SESION_CANCELADA");
    }

    public void eliminarRecordatorios(SesionEstudio sesion) {
        notificaciones.eliminarRecordatoriosPendientes(sesion);
    }

    // Ruta antigua conservada; ahora lee la misma tabla de la bandeja general.
    @Transactional(readOnly = true)
    public List<NotificacionResponse> listarNotificaciones(Long idEstudiante) {
        Long id = usuarioActual.exigirPropio(idEstudiante).getIdUsuario();
        usuarioActual.estudianteActual();
        return repositorio.findByDestinatario_IdUsuarioAndSesionIsNotNullOrderByFechaCreacionDesc(id)
                .stream().map(n -> {
                    var sesion = n.getSesion();
                    String titulo = switch (n.getTipo()) {
                        case "SESION_RECORDATORIO" -> "Recordatorio: tienes una sesión próxima";
                        case "SESION_CANCELADA" -> "La sesión fue cancelada";
                        default -> "La sesión fue modificada";
                    };
                    return new NotificacionResponse(n.getIdNotificacion(), sesion.getIdSesion(),
                            n.getTipo(), titulo, sesion.getGrupo().getNombre(), sesion.getTema(),
                            sesion.getFechaHoraInicio().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                            sesion.getFechaHoraInicio().format(DateTimeFormatter.ofPattern("HH:mm")),
                            sesion.getLugar(), n.getFechaCreacion());
                }).toList();
    }
}
