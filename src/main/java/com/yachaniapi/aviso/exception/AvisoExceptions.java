package com.yachaniapi.aviso.exception;

public class AvisoExceptions {

    public static class AvisoNoEncontradoException extends RuntimeException {
        public AvisoNoEncontradoException(String mensaje) {
            super(mensaje);
        }
    }

    public static class AutorNoAutorizadoException extends RuntimeException {
        public AutorNoAutorizadoException(String mensaje) {
            super(mensaje);
        }
    }
}
