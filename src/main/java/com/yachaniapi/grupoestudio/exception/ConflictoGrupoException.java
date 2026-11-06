package com.yachaniapi.grupoestudio.exception;

public class ConflictoGrupoException extends RuntimeException {
    private final String codigo;

    public ConflictoGrupoException(String mensaje) {
        this("CONFLICTO_GRUPO", mensaje);
    }

    public ConflictoGrupoException(String codigo, String mensaje) {
        super(mensaje);
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }
}
