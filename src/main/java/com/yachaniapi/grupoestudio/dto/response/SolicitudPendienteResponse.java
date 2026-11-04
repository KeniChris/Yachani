package com.yachaniapi.grupoestudio.dto.response;

import java.time.LocalDateTime;

public record SolicitudPendienteResponse(
        Long idSolicitud,
        Long idGrupo,
        Long idEstudiante,
        String nombreEstudiante,
        String estado,
        LocalDateTime fechaSolicitud
) {}
