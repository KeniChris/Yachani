package com.yachaniapi.notificacion.mapper;

import com.yachaniapi.grupoestudio.entity.GrupoEstudio;
import com.yachaniapi.notificacion.dto.NotificacionResponse;
import com.yachaniapi.notificacion.entity.Notificacion;
import org.springframework.stereotype.Component;

@Component
public class NotificacionMapper {
    public NotificacionResponse toNotificacionResponse(Notificacion n) {
        GrupoEstudio grupo = n.getGrupo();
        if (grupo == null && n.getSolicitud() != null) grupo = n.getSolicitud().getGrupo();
        if (grupo == null && n.getSesion() != null) grupo = n.getSesion().getGrupo();
        Long idGrupo = grupo == null ? null : grupo.getIdGrupo();
        String titulo = switch (n.getTipo()) {
            case "SOLICITUD_RECIBIDA" -> "Nueva solicitud de ingreso";
            case "SOLICITUD_ACEPTADA" -> "Solicitud aceptada";
            case "SOLICITUD_RECHAZADA" -> "Solicitud rechazada";
            case "SESION_RECORDATORIO" -> "Tu sesión empieza pronto";
            case "SESION_MODIFICADA" -> "La sesión fue modificada";
            case "SESION_CANCELADA" -> "La sesión fue cancelada";
            default -> "Notificación";
        };
        String ruta = "/notificaciones";
        if (idGrupo != null) {
            ruta = "/grupos/" + idGrupo;
            if ("SOLICITUD_RECIBIDA".equals(n.getTipo())) ruta += "/solicitudes";
            if (n.getSesion() != null) ruta += "/sesiones";
        }
        if ("SOLICITUD_RECHAZADA".equals(n.getTipo())) ruta = "/grupos/descubrimiento";
        return new NotificacionResponse(n.getIdNotificacion(),
                n.getSolicitud() == null ? null : n.getSolicitud().getIdSolicitud(),
                idGrupo, grupo == null ? null : grupo.getNombre(), n.getTipo(),
                n.getMensaje(), n.getLeida(), n.getFechaCreacion(),
                n.getSesion() == null ? null : n.getSesion().getIdSesion(), titulo, ruta);
    }
}
