package com.yachaniapi.recursos.dto.request;

import jakarta.validation.constraints.Size;

public record TemaActualizarRequest(
        @Size(
                min = 1,
                max = 100,
                message = "El nombre admite entre 1 y 100 caracteres"
        )
        String nombre,

        @Size(
                max = 300,
                message = "La descripción admite hasta 300 caracteres"
        )
        String descripcion
) {}