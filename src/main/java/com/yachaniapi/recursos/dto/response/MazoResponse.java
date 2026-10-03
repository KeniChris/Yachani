package com.yachaniapi.recursos.dto.response;

import com.yachaniapi.recursos.entity.EstadoMazo;

import java.time.LocalDateTime;

public record MazoResponse(
        Long idMazo,
        Long idAutor,
        String autor,
        String titulo,
        String descripcion,
        EstadoMazo estado,
        long cantidadTarjetas,
        LocalDateTime fechaCreacion
) {
}