package com.yachaniapi.contacto.exception;

public class LimiteContactoException extends RuntimeException {
    private final long segundosRestantes;

    public LimiteContactoException(long segundosRestantes) {
        super("Has alcanzado el límite de mensajes. Inténtalo de nuevo en unos minutos.");
        this.segundosRestantes = segundosRestantes;
    }

    public long getSegundosRestantes() {
        return segundosRestantes;
    }
}
