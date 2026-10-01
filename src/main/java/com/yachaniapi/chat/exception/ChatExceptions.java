package com.yachaniapi.chat.exception;

public class ChatExceptions {

    public static class UsuarioNoPerteneceAlGrupoException extends RuntimeException {
        public UsuarioNoPerteneceAlGrupoException(String mensaje) {
            super(mensaje);
        }
    }

    public static class MensajeNoEncontradoException extends RuntimeException {
        public MensajeNoEncontradoException(String mensaje) {
            super(mensaje);
        }
    }

    public static class MensajeAjenoException extends RuntimeException {
        public MensajeAjenoException(String mensaje) {
            super(mensaje);
        }
    }

    public static class TiempoEliminacionExpiradoException extends RuntimeException {
        public TiempoEliminacionExpiradoException(String mensaje) {
            super(mensaje);
        }
    }
}