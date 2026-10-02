package com.yachaniapi.usuario.exception;

public class UsuarioNoEsEstudianteException extends RuntimeException {

    public UsuarioNoEsEstudianteException(String mensaje) {
        super(mensaje);
    }
}