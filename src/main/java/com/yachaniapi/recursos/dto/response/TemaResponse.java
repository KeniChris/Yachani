package com.yachaniapi.recursos.dto.response;

import java.time.LocalDateTime;

public record TemaResponse(
        Long idTema,
        Long idGrupo,
        String nombre,
        String descripcion,
        Long idCreador,
        LocalDateTime fechaCreacion
) {}