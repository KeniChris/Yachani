package com.yachaniapi.recursos.dto.response;

import java.time.LocalDateTime;

public record EstadoTarjetaResponse(
        Long idTarjeta,
        boolean vista,
        boolean aprendida,
        LocalDateTime fechaUltimaPractica
) {
}