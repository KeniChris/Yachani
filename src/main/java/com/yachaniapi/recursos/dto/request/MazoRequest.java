package com.yachaniapi.recursos.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MazoRequest(

        @NotBlank(message = "El título es obligatorio")
        @Size(
                max = 120,
                message = "El título admite hasta 120 caracteres"
        )
        String titulo,

        @Size(
                max = 500,
                message = "La descripción admite hasta 500 caracteres"
        )
        String descripcion
) {
}