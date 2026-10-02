package com.yachaniapi.usuario.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Información académica que el backend devolverá al consultar el perfil de un estudiante
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PerfilAcademicoResponse {

    private Long idUsuario;
    private String nombres;
    private String apellidos;
    private String correo;

    private String universidad;
    private String carrera;

    private String modalidadPreferida;
    private String metodoPreferido;
    private List<String> temasInteres;

    private List<ConocimientoAcademicoResponse> conocimientos;
}