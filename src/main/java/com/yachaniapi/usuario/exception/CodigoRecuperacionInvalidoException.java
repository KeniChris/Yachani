package com.yachaniapi.usuario.exception;

public class CodigoRecuperacionInvalidoException
        extends RuntimeException {

    public CodigoRecuperacionInvalidoException(String mensaje) {
        super(mensaje);
    }
}