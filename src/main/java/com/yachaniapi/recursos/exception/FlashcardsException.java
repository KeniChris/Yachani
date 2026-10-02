package com.yachaniapi.recursos.exception;

import org.springframework.http.HttpStatus;

public class FlashcardsException extends RecursosException {

    public FlashcardsException(
            HttpStatus estado,
            String mensaje
    ) {
        super(estado, mensaje);
    }
}