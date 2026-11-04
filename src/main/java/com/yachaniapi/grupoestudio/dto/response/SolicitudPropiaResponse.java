package com.yachaniapi.grupoestudio.dto.response;

import java.time.LocalDateTime;

public record SolicitudPropiaResponse(
        Long idSolicitud,
        Long idGrupo,
        String nombreGrupo,
        String estado,
        LocalDateTime fechaSolicitud
) {}
