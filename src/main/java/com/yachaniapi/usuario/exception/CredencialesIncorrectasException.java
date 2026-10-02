package com.yachaniapi.usuario.exception;

public class CredencialesIncorrectasException extends RuntimeException {

    public CredencialesIncorrectasException(String mensaje) {
        super(mensaje);
    }
}