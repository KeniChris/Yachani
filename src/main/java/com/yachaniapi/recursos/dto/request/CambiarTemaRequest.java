package com.yachaniapi.recursos.dto.request;

import jakarta.validation.constraints.Positive;

public record CambiarTemaRequest(
        @Positive(message = "El ID del tema debe ser positivo")
        Long idTema
) {}