package com.yachaniapi.recursos.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CompartirMazoRequest(

        @NotNull(message = "Selecciona un tema")
        @Positive(message = "El ID del tema debe ser positivo")
        Long idTema
) {
}