package com.yachaniapi.sesion.mapper;

import com.yachaniapi.sesion.dto.SesionResponse;
import com.yachaniapi.sesion.entity.SesionEstudio;
import org.springframework.stereotype.Component;
import com.yachaniapi.sesion.dto.NotificacionResponse;
import com.yachaniapi.sesion.entity.NotificacionSesion;
import com.yachaniapi.sesion.service.NotificacionSesionService;

import java.time.format.DateTimeFormatter;
@Component
public class SesionEstudioMapper {

    public SesionResponse toSesionResponse(SesionEstudio sesion, String mensaje) {
        return new SesionResponse(
                sesion.getIdSesion(),
                sesion.getGrupo().getIdGrupo(),
                sesion.getGrupo().getNombre(),
                sesion.getTema(),
                sesion.getDescripcion(),
                sesion.getFechaHoraInicio(),
                sesion.getDuracionMinutos(),
                sesion.getLugar(),
                sesion.getEstado(),
                mensaje
        );
    }
    
    public NotificacionResponse toNotificacionResponse(NotificacionSesion notificacion) {

        SesionEstudio sesion = notificacion.getSesion();

        String titulo = NotificacionSesionService.TIPO_RECORDATORIO.equals(notificacion.getTipo())
                ? "Recordatorio: tienes una sesión próxima"
                : "La sesión fue modificada";

        return new NotificacionResponse(
                notificacion.getIdNotificacion(),
                sesion.getIdSesion(),
                notificacion.getTipo(),
                titulo,
                sesion.getGrupo().getNombre(),
                sesion.getTema(),
                sesion.getFechaHoraInicio().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                sesion.getFechaHoraInicio().format(DateTimeFormatter.ofPattern("HH:mm")),
                sesion.getLugar(),
                notificacion.getFechaCreacion()
        );
    }
}