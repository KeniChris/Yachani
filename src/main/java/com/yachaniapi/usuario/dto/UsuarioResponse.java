package com.yachaniapi.usuario.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Información que el backend devuelve después del registro o inicio de sesión.
 * excepto la contraseña del usuario.
 */
@Getter
@AllArgsConstructor
public class UsuarioResponse {

    private Long idUsuario;
    private String nombres;
    private String apellidos;
    private String correo;
    private String tipoUsuario;
    private String mensaje;
}