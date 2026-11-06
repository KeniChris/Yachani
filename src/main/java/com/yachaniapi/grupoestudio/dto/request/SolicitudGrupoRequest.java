package com.yachaniapi.grupoestudio.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record SolicitudGrupoRequest(
        @Positive(message = "El ID del estudiante debe ser positivo")
        Long idEstudiante
) {}
