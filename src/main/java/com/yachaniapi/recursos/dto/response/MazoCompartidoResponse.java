package com.yachaniapi.recursos.dto.response;

import java.time.LocalDateTime;

public record MazoCompartidoResponse(
        Long idCompartido,
        Long idGrupo,
        Long idTema,
        String nombreTema,
        MazoResponse mazo,
        LocalDateTime fechaCompartido
) {
}