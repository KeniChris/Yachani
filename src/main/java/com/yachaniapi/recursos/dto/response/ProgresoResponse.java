package com.yachaniapi.recursos.dto.response;

import java.util.List;

public record ProgresoResponse(
        Long idMazo,
        long totalTarjetas,
        long tarjetasVistas,
        long tarjetasAprendidas,
        double porcentajeRevisado,
        boolean completado,
        Long idSiguienteTarjeta,
        List<EstadoTarjetaResponse> tarjetas
) {
}