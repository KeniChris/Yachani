package com.yachaniapi.grupoestudio.exception;

public class GrupoEstudioExceptions {

    public static class GrupoLlenoException extends RuntimeException {
        public GrupoLlenoException(String mensaje) {
            super(mensaje);
        }
    }

    public static class GrupoNoEncontradoException extends RuntimeException {
        public GrupoNoEncontradoException(String mensaje) {
            super(mensaje);
        }
    }

    public static class SolicitudNoEncontradaException extends RuntimeException {
        public SolicitudNoEncontradaException(String mensaje) {
            super(mensaje);
        }
    }
}
