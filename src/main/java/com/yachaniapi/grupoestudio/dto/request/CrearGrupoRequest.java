package com.yachaniapi.grupoestudio.dto.request;

import jakarta.validation.constraints.*;

public record CrearGrupoRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 100, message = "El nombre admite hasta 100 caracteres")
        String nombre,
        @NotBlank(message = "La descripción es obligatoria")
        @Size(max = 500, message = "La descripción admite hasta 500 caracteres")
        String descripcion,
        @NotBlank(message = "El método de estudio es obligatorio")
        @Size(max = 50, message = "El método admite hasta 50 caracteres")
        String metodoEstudio,
        @NotNull(message = "La capacidad es obligatoria")
        @Min(value = 2, message = "La capacidad mínima es de 2 estudiantes")
        Integer capacidadMaxima,
        @Positive(message = "El ID del creador debe ser positivo")
        Long idCreador
) {}
