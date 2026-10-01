package com.yachaniapi.sesion.exception;

public class SesionExceptions {

    public static class SesionNoEncontradaException extends RuntimeException {
        public SesionNoEncontradaException(String mensaje) {
            super(mensaje);
        }
    }

    public static class TutorNoAutorizadoException extends RuntimeException {
        public TutorNoAutorizadoException(String mensaje) {
            super(mensaje);
        }
    }

    public static class SesionCanceladaException extends RuntimeException {
        public SesionCanceladaException(String mensaje) {
            super(mensaje);
        }
    }
}