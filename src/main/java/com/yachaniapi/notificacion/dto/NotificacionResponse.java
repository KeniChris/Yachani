package com.yachaniapi.notificacion.dto;

import java.time.LocalDateTime;

public record NotificacionResponse(
        Long idNotificacion, Long idSolicitud, Long idGrupo, String nombreGrupo,
        String tipo, String mensaje, Boolean leida, LocalDateTime fechaCreacion,
        Long idSesion, String titulo, String ruta
) {}
