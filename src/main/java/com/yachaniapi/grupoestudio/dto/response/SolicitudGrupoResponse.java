package com.yachaniapi.grupoestudio.dto.response;

public record SolicitudGrupoResponse(
        Long idSolicitud,
        Long idGrupo,
        String estado,
        String mensaje
) {}
