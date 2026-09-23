package com.yachaniapi.usuario.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Datos necesarios para registrar o actualizar las preferencias de estudio del estudiante
 */
@Getter
@Setter
@NoArgsConstructor
public class PreferenciasEstudioRequest {

    @NotBlank(message = "La modalidad preferida es obligatoria")
    private String modalidadPreferida;

    @NotBlank(message = "El método de estudio preferido es obligatorio")
    private String metodoPreferido;

    @NotNull(message = "La lista de temas de interés es obligatoria")
    private List<String> temasInteres;
}