package com.yachaniapi.usuario.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Datos que el frontend enviará para registrar un usuario.
 */
@Getter
@Setter
@NoArgsConstructor
public class RegistroRequest {

    private String nombres;
    private String apellidos;
    private String correo;
    private String contrasena;
    // ESTUDIANTE o TUTOR.
    private String tipoUsuario;
}