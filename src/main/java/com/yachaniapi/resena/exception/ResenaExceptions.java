package com.yachaniapi.resena.exception;

public class ResenaExceptions {

    public static class UsuarioNoEsTutorException extends RuntimeException {
        public UsuarioNoEsTutorException(String mensaje) {
            super(mensaje);
        }
    }

    public static class ResenaDuplicadaException extends RuntimeException {
        public ResenaDuplicadaException(String mensaje) {
            super(mensaje);
        }
    }

    public static class ResenaNoPermitidaException extends RuntimeException {
        public ResenaNoPermitidaException(String mensaje) {
            super(mensaje);
        }
    }
}