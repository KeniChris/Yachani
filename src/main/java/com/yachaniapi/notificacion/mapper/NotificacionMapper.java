package com.yachaniapi.notificacion.mapper;

import com.yachaniapi.notificacion.dto.NotificacionResponse;
import com.yachaniapi.notificacion.entity.Notificacion;
import org.springframework.stereotype.Component;

@Component
public class NotificacionMapper {

    public NotificacionResponse toNotificacionResponse(Notificacion notificacion) {

        return new NotificacionResponse(
                notificacion.getIdNotificacion(),
                notificacion.getSolicitud().getIdSolicitud(),
                notificacion.getSolicitud().getGrupo().getIdGrupo(),
                notificacion.getSolicitud().getGrupo().getNombre(),
                notificacion.getTipo(),
                notificacion.getMensaje(),
                notificacion.getLeida(),
                notificacion.getFechaCreacion()
        );
    }
}
