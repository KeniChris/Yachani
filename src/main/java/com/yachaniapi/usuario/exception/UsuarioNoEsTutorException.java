package com.yachaniapi.usuario.exception;

public class UsuarioNoEsTutorException extends RuntimeException {

    public UsuarioNoEsTutorException(String mensaje) {
        super(mensaje);
    }
}