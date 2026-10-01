package com.yachaniapi.recursos.exception;

import org.springframework.http.HttpStatus;

public class RecursosException extends RuntimeException {

    private final HttpStatus estado;

    public RecursosException(HttpStatus estado, String mensaje) {
        super(mensaje);
        this.estado = estado;
    }

    public HttpStatus getEstado() {
        return estado;
    }
}