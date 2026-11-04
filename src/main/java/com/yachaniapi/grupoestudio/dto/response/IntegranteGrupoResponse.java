package com.yachaniapi.grupoestudio.dto.response;

public record IntegranteGrupoResponse(
        Long idUsuario,
        String nombreCompleto,
        String correo,
        String tipoUsuario,
        boolean administrador
) {}
