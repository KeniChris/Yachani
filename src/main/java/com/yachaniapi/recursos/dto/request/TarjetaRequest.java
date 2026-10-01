package com.yachaniapi.recursos.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TarjetaRequest(

        @NotBlank(message = "La pregunta es obligatoria")
        @Size(
                max = 1000,
                message = "La pregunta admite hasta 1000 caracteres"
        )
        String pregunta,

        @NotBlank(message = "La respuesta es obligatoria")
        @Size(
                max = 1000,
                message = "La respuesta admite hasta 1000 caracteres"
        )
        String respuesta
) {
}