package com.yachaniapi.notificacion.mapper;

import com.yachaniapi.grupoestudio.entity.GrupoEstudio;
import com.yachaniapi.grupoestudio.entity.SolicitudGrupo;
import com.yachaniapi.notificacion.dto.NotificacionResponse;
import com.yachaniapi.notificacion.entity.Notificacion;
import org.springframework.stereotype.Component;

@Component
public class NotificacionMapper {

    public NotificacionResponse toNotificacionResponse(Notificacion notificacion) {

        // La solicitud puede haberse borrado (al aceptarla se elimina), así que el grupo se
        // toma de la propia notificación y la solicitud se lee solo si sigue existiendo.
        SolicitudGrupo solicitud = notificacion.getSolicitud();
        GrupoEstudio grupo = (notificacion.getGrupo() != null)
                ? notificacion.getGrupo()
                : (solicitud != null ? solicitud.getGrupo() : null);

        return new NotificacionResponse(
                notificacion.getIdNotificacion(),
                (solicitud != null) ? solicitud.getIdSolicitud() : null,
                (grupo != null) ? grupo.getIdGrupo() : null,
                (grupo != null) ? grupo.getNombre() : null,
                notificacion.getTipo(),
                notificacion.getMensaje(),
                notificacion.getLeida(),
                notificacion.getFechaCreacion()
        );
    }
}
