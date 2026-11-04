package com.yachaniapi.usuario.dto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;

public record ConfiguracionEstudianteRequest(
        @NotBlank @Size(max = 120) String universidad,
        @NotBlank @Size(max = 120) String carrera,
        @NotEmpty List<@NotNull @Valid ConocimientoAcademicoRequest> conocimientos,
        @NotBlank @Size(max = 50) String modalidadPreferida,
        @NotBlank @Size(max = 50) String metodoPreferido,
        @NotNull List<@NotBlank @Size(max = 100) String> temasInteres
) {}
