package com.yachaniapi.recursos.dto.response;

public record TarjetaResponse(
        Long idTarjeta,
        String pregunta,
        String respuesta,
        Integer orden
) {
}