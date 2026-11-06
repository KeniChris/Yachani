package com.yachaniapi.notificacion;

import com.yachaniapi.grupoestudio.entity.GrupoEstudio;
import com.yachaniapi.notificacion.entity.Notificacion;
import com.yachaniapi.notificacion.mapper.NotificacionMapper;
import com.yachaniapi.sesion.entity.SesionEstudio;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class NotificacionMapperTest {
    @Test
    void unaNotificacionDeSesionNoNecesitaSolicitudDeIngreso() {
        var grupo = new GrupoEstudio(); grupo.setIdGrupo(10L); grupo.setNombre("Cálculo");
        var sesion = new SesionEstudio(); sesion.setIdSesion(20L); sesion.setGrupo(grupo);
        var n = new Notificacion(); n.setIdNotificacion(1L);
        n.setSesion(sesion); n.setTipo("SESION_RECORDATORIO"); n.setMensaje("Empieza pronto");
        var respuesta = new NotificacionMapper().toNotificacionResponse(n);
        assertNull(respuesta.idSolicitud());
        assertEquals(10L, respuesta.idGrupo());
        assertEquals(20L, respuesta.idSesion());
        assertEquals("/grupos/10/sesiones", respuesta.ruta());
        assertEquals("Tu sesión empieza pronto", respuesta.titulo());
    }
}
