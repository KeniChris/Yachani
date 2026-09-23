package com.yachaniapi.usuario.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Datos necesarios para registrar o actualizar un curso y el nivel de conocimiento de un estudiante
 */
@Getter
@Setter
@NoArgsConstructor
public class ConocimientoAcademicoRequest {

    @NotBlank(message = "El curso es obligatorio")
    private String curso;

    @NotBlank(message = "El nivel de conocimiento es obligatorio")
    private String nivel;
}