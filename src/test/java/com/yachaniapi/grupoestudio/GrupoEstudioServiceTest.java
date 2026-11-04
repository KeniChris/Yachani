package com.yachaniapi.grupoestudio;

import com.yachaniapi.grupoestudio.dto.request.*;
import com.yachaniapi.grupoestudio.entity.*;
import com.yachaniapi.grupoestudio.exception.ConflictoGrupoException;
import com.yachaniapi.grupoestudio.exception.GrupoEstudioExceptions.GrupoLlenoException;
import com.yachaniapi.grupoestudio.mapper.GrupoEstudioMapper;
import com.yachaniapi.grupoestudio.repository.*;
import com.yachaniapi.grupoestudio.service.*;
import com.yachaniapi.notificacion.service.NotificacionService;
import com.yachaniapi.usuario.entity.*;
import com.yachaniapi.usuario.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GrupoEstudioServiceTest {

    @Mock GrupoEstudioRepository grupos;
    @Mock SolicitudGrupoRepository solicitudes;
    @Mock UsuarioRepository usuarios;
    @Mock NotificacionService notificaciones;
    @Mock com.yachaniapi.usuario.service.UsuarioActualService usuarioActual;
    @Mock AccesoGrupoService acceso;

    private GrupoEstudioService service;

    @BeforeEach
    void preparar() {
        service = new GrupoEstudioService(
                grupos, solicitudes, usuarios, notificaciones,
                Mappers.getMapper(GrupoEstudioMapper.class), new ReglasGrupoService(), usuarioActual, acceso
        );
    }

    @Test
    void crearGrupoEstudianteIncluyeAlCreadorYMapeaLaRespuesta() {
        Estudiante creador = estudiante(1L);
        when(grupos.bloquearCreador(1L)).thenReturn(Optional.of(1L));
        when(usuarioActual.exigirPropio(1L)).thenReturn(creador);
        when(grupos.save(any(GrupoEstudio.class))).thenAnswer(invocacion -> {
            GrupoEstudio grupo = invocacion.getArgument(0);
            grupo.setIdGrupo(10L);
            return grupo;
        });

        var respuesta = service.crearGrupo(new CrearGrupoRequest(
                " Cálculo ", " Ejercicios ", " Práctica ", 3, 1L));

        assertEquals(10L, respuesta.idGrupo());
        assertEquals("Cálculo", respuesta.nombre());
        assertEquals(1, respuesta.integrantesActuales());
        assertEquals(2, respuesta.cuposDisponibles());
        assertEquals("Ana Pérez", respuesta.nombreCreador());
        assertEquals("ESTUDIANTE", respuesta.tipoCreador());
        assertTrue(respuesta.permitirPublicaciones());
    }

    @Test
    void noPermiteCrearMasGruposQueElLimiteActual() {
        when(grupos.bloquearCreador(1L)).thenReturn(Optional.of(1L));
        when(usuarioActual.exigirPropio(1L)).thenReturn(estudiante(1L));
        when(grupos.countByCreador_IdUsuario(1L)).thenReturn(5L);

        assertThrows(ConflictoGrupoException.class, () -> service.crearGrupo(
                new CrearGrupoRequest("Grupo", "Descripción", "Práctica", 3, 1L)));
        verify(grupos, never()).save(any());
    }

    @Test
    void alOcuparseElUltimoCupoLaSiguienteAceptacionSeRechaza() {
        GrupoEstudio grupo = grupoEstudiantes(2);
        SolicitudGrupo primera = solicitud(10L, grupo, estudiante(2L));
        SolicitudGrupo segunda = solicitud(11L, grupo, estudiante(3L));
        when(solicitudes.buscarIdGrupo(10L)).thenReturn(Optional.of(20L));
        when(solicitudes.buscarIdGrupo(11L)).thenReturn(Optional.of(20L));
        when(acceso.administrador(20L, true)).thenReturn(grupo);
        when(solicitudes.findById(10L)).thenReturn(Optional.of(primera));
        when(solicitudes.findById(11L)).thenReturn(Optional.of(segunda));

        assertEquals("ACEPTADA", service.aceptarSolicitud(10L).estado());
        assertThrows(GrupoLlenoException.class, () -> service.aceptarSolicitud(11L));
        assertEquals("PENDIENTE", segunda.getEstado());
        assertEquals(2, grupo.getParticipantes().size());
        verify(notificaciones).notificarSolicitudAceptada(primera);
        verify(notificaciones, never()).notificarSolicitudAceptada(segunda);
    }

    @Test
    void noProcesaOtraVezUnaSolicitudYaAceptada() {
        GrupoEstudio grupo = grupoEstudiantes(3);
        SolicitudGrupo solicitud = solicitud(10L, grupo, estudiante(2L));
        solicitud.setEstado("ACEPTADA");
        when(solicitudes.buscarIdGrupo(10L)).thenReturn(Optional.of(20L));
        when(acceso.administrador(20L, true)).thenReturn(grupo);
        when(solicitudes.findById(10L)).thenReturn(Optional.of(solicitud));

        assertThrows(ConflictoGrupoException.class, () -> service.aceptarSolicitud(10L));
        verifyNoInteractions(notificaciones);
    }

    @Test
    void noReduceCapacidadPorDebajoDeLosInscritos() {
        GrupoEstudio grupo = grupoEstudiantes(5);
        grupo.getParticipantes().add(estudiante(2L));
        grupo.getParticipantes().add(estudiante(3L));
        when(acceso.administrador(20L, true)).thenReturn(grupo);

        assertThrows(ConflictoGrupoException.class,
                () -> service.actualizarCapacidad(20L, new EditarCapacidadGrupoRequest(2)));
        assertEquals(5, grupo.getCapacidadMaxima());
    }

    @Test
    void noPermiteCerrarPublicacionesDeUnGrupoEntreEstudiantes() {
        GrupoEstudio grupo = grupoEstudiantes(3);
        when(acceso.administrador(20L, true)).thenReturn(grupo);

        assertThrows(IllegalArgumentException.class, () -> service.cambiarPermitirPublicaciones(
                20L, new EditarPublicacionesGrupoRequest(false)));
        assertTrue(grupo.getPermitirPublicaciones());
    }

    private GrupoEstudio grupoEstudiantes(int capacidad) {
        Estudiante creador = estudiante(1L);
        GrupoEstudio grupo = new GrupoEstudio();
        grupo.setIdGrupo(20L);
        grupo.setNombre("Grupo");
        grupo.setCreador(creador);
        grupo.setTipoGrupo(TipoGrupo.ESTUDIANTES);
        grupo.setEstado("ACTIVO");
        grupo.setCapacidadMaxima(capacidad);
        grupo.getParticipantes().add(creador);
        return grupo;
    }

    private SolicitudGrupo solicitud(Long id, GrupoEstudio grupo, Estudiante estudiante) {
        SolicitudGrupo solicitud = new SolicitudGrupo();
        solicitud.setIdSolicitud(id);
        solicitud.setGrupo(grupo);
        solicitud.setEstudiante(estudiante);
        solicitud.setEstado("PENDIENTE");
        return solicitud;
    }

    private Estudiante estudiante(Long id) {
        Estudiante estudiante = new Estudiante();
        estudiante.setIdUsuario(id);
        estudiante.setNombres("Ana");
        estudiante.setApellidos("Pérez");
        estudiante.setCorreo("ana" + id + "@example.com");
        return estudiante;
    }
}
