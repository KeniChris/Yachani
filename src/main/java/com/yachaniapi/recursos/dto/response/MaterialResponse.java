package com.yachaniapi.recursos.dto.response;

import java.time.LocalDateTime;

public record MaterialResponse(
        Long idMaterial,
        Long idGrupo,
        Long idTema,
        String nombreTema,
        Long idAutor,
        String autor,
        String nombreArchivo,
        String tipoArchivo,
        Long tamanoArchivo,
        LocalDateTime fechaPublicacion
) {}