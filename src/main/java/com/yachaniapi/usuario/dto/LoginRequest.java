package com.yachaniapi.usuario.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Credenciales enviadas para iniciar sesión.
 */
@Getter
@Setter
@NoArgsConstructor
public class LoginRequest {

    private String correo;
    private String contrasena;
}