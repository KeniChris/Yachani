package com.yachaniapi.grupoestudio;

import com.yachaniapi.exception.GlobalExceptionHandler;
import com.yachaniapi.grupoestudio.exception.ConflictoGrupoException;
import com.yachaniapi.grupoestudio.exception.GrupoEstudioExceptions.GrupoLlenoException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ErroresIngresoGrupoTest {
    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test void grupoLlenoSeDistingueDeSolicitudPendiente() {
        var lleno = handler.manejarGrupoLleno(new GrupoLlenoException("El grupo alcanzó su capacidad"));
        var pendiente = handler.manejarConflictoGrupo(
                new ConflictoGrupoException("SOLICITUD_PENDIENTE", "Ya tienes una solicitud pendiente"));
        assertEquals(409, lleno.getStatusCode().value());
        assertEquals("GRUPO_LLENO", lleno.getBody().get("codigo"));
        assertEquals("SOLICITUD_PENDIENTE", pendiente.getBody().get("codigo"));
    }
}
