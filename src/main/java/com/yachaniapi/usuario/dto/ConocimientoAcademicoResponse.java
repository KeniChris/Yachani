package com.yachaniapi.usuario.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Datos del curso y nivel de conocimiento que el backend devolverá al frontend
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConocimientoAcademicoResponse {

    private Long idConocimiento;
    private String curso;
    private String nivel;
}