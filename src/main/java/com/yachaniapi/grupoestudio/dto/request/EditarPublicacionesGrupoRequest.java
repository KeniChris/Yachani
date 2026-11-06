package com.yachaniapi.grupoestudio.dto.request;

import jakarta.validation.constraints.NotNull;

public record EditarPublicacionesGrupoRequest(
        @NotNull(message = "Indica si se permiten publicaciones")
        Boolean permitirPublicaciones
) {}
