package com.yachaniapi.recursos.dto.response;

import java.util.List;

public record MazoDetalleResponse(
        MazoResponse mazo,
        List<TarjetaResponse> tarjetas
) {
}