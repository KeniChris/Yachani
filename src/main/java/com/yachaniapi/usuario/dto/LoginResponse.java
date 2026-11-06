package com.yachaniapi.usuario.dto;

import lombok.Getter;

@Getter
public class LoginResponse extends UsuarioResponse {
    private final String accessToken;
    private final String tokenType;
    private final long expiresIn;

    public LoginResponse(UsuarioResponse usuario, String accessToken, long expiresIn) {
        super(usuario.getIdUsuario(), usuario.getNombres(), usuario.getApellidos(),
                usuario.getCorreo(), usuario.getTipoUsuario(), usuario.getMensaje(),
                usuario.isConfiguracionCompletada());
        this.accessToken = accessToken;
        this.tokenType = "Bearer";
        this.expiresIn = expiresIn;
    }
}
