package com.yachaniapi.usuario.exception;

public class EnvioCorreoException extends RuntimeException {

    public EnvioCorreoException(String mensaje) {
        super(mensaje);
    }
}