package com.yachaniapi.notificacion.exception;

public class NotificacionExceptions {

    public static class NotificacionNoEncontradaException extends RuntimeException {
        public NotificacionNoEncontradaException(String mensaje) {
            super(mensaje);
        }
    }
}
