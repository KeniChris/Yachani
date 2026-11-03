package com.yachaniapi.recursos.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TemaRequest(
        @NotBlank(message = "El nombre del tema es obligatorio")
        @Size(
                max = 100,
                message = "El nombre admite hasta 100 caracteres"
        )
        String nombre,

        @Size(
                max = 300,
                message = "La descripción admite hasta 300 caracteres"
        )
        String descripcion
) {}