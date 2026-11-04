package com.yachaniapi.grupoestudio.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record EditarCapacidadGrupoRequest(
        @NotNull(message = "La capacidad es obligatoria")
        @Min(value = 2, message = "La capacidad mínima es de 2 estudiantes")
        Integer nuevaCapacidad
) {}
