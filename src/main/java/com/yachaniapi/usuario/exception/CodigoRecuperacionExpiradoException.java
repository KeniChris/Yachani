package com.yachaniapi.usuario.exception;

public class CodigoRecuperacionExpiradoException
        extends RuntimeException {

    public CodigoRecuperacionExpiradoException(String mensaje) {
        super(mensaje);
    }
}