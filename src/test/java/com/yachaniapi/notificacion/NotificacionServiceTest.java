package com.yachaniapi.notificacion;

import com.yachaniapi.grupoestudio.entity.GrupoEstudio;
import com.yachaniapi.grupoestudio.entity.SolicitudGrupo;
import com.yachaniapi.notificacion.entity.Notificacion;
import com.yachaniapi.notificacion.mapper.NotificacionMapper;
import com.yachaniapi.notificacion.repository.NotificacionRepository;
import com.yachaniapi.notificacion.service.NotificacionService;
import com.yachaniapi.usuario.service.UsuarioActualService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificacionServiceTest {
    @Mock NotificacionRepository notificaciones;
    @Mock UsuarioActualService usuarioActual;
    private NotificacionService service;

    @BeforeEach
    void preparar() {
        service = new NotificacionService(notificaciones, usuarioActual, new NotificacionMapper());
    }

    @Test
    void desvincularConservaElContextoDeNotificacionesAntiguasYNoLasElimina() {
        var grupo = new GrupoEstudio();
        grupo.setIdGrupo(20L);
        grupo.setNombre("Cálculo");
        var solicitud = new SolicitudGrupo();
        solicitud.setIdSolicitud(10L);
        solicitud.setGrupo(grupo);
        var notificacion = new Notificacion();
        notificacion.setIdNotificacion(30L);
        notificacion.setSolicitud(solicitud);
        notificacion.setTipo("SOLICITUD_ACEPTADA");
        notificacion.setMensaje("Tu solicitud fue aceptada");
        notificacion.setLeida(true);
        var lista = List.of(notificacion);
        when(notificaciones.findBySolicitud_IdSolicitud(10L)).thenReturn(lista);

        service.desvincularDeSolicitud(10L);

        assertNull(notificacion.getSolicitud());
        assertSame(grupo, notificacion.getGrupo());
        assertTrue(notificacion.getLeida());
        var respuesta = new NotificacionMapper().toNotificacionResponse(notificacion);
        assertEquals(20L, respuesta.idGrupo());
        assertEquals("Cálculo", respuesta.nombreGrupo());
        assertEquals("/grupos/20", respuesta.ruta());
        verify(notificaciones).saveAllAndFlush(lista);
        verify(notificaciones, never()).delete(any());
    }

    @Test
    void desvincularUnaSolicitudSinNotificacionesEsValido() {
        when(notificaciones.findBySolicitud_IdSolicitud(10L)).thenReturn(List.of());

        assertDoesNotThrow(() -> service.desvincularDeSolicitud(10L));

        verify(notificaciones).saveAllAndFlush(List.of());
        verifyNoInteractions(usuarioActual);
    }
}
