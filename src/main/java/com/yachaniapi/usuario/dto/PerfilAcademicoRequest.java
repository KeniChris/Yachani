package com.yachaniapi.usuario.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * datos necesarios para registrar o actualizar
 * el perfil académico de un estudiante.
 */
@Getter
@Setter
@NoArgsConstructor
public class PerfilAcademicoRequest {

    @NotBlank(message = "La universidad es obligatoria")
    private String universidad;

    @NotBlank(message = "La carrera es obligatoria")
    private String carrera;
}