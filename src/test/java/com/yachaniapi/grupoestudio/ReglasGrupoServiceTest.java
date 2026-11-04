package com.yachaniapi.grupoestudio;

import com.yachaniapi.grupoestudio.entity.GrupoEstudio;
import com.yachaniapi.grupoestudio.entity.TipoGrupo;
import com.yachaniapi.grupoestudio.service.ReglasGrupoService;
import com.yachaniapi.usuario.entity.Estudiante;
import com.yachaniapi.usuario.entity.Tutor;
import com.yachaniapi.usuario.entity.Usuario;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReglasGrupoServiceTest {

    private final ReglasGrupoService reglas = new ReglasGrupoService();

    @Test
    void creadorEstudianteCuentaUnaVez() {
        Estudiante creador = estudiante(1L);
        GrupoEstudio grupo = grupo(TipoGrupo.ESTUDIANTES, creador);
        assertEquals(1, reglas.integrantesActuales(grupo));
        grupo.getParticipantes().add(creador);
        assertEquals(1, reglas.integrantesActuales(grupo));
        assertEquals(1, reglas.cuposDisponibles(grupo));
    }

    @Test
    void tutorNoConsumeCupoDeEstudiante() {
        GrupoEstudio grupo = grupo(TipoGrupo.TUTOR, tutor(1L));
        assertEquals(0, reglas.integrantesActuales(grupo));
        assertEquals(2, reglas.cuposDisponibles(grupo));
    }

    @Test
    void publicacionesCerradasPermitenAlTutorPeroNoAlIntegrante() {
        Tutor tutor = tutor(1L);
        Estudiante alumno = estudiante(2L);
        GrupoEstudio grupo = grupo(TipoGrupo.TUTOR, tutor);
        grupo.getParticipantes().add(alumno);
        grupo.setPermitirPublicaciones(false);
        assertTrue(reglas.puedePublicarRecursos(grupo, tutor));
        assertTrue(reglas.puedeGestionarTemas(grupo, tutor));
        assertFalse(reglas.puedePublicarRecursos(grupo, alumno));
    }

    @Test
    void publicacionesAbiertasNoDanAccesoAUnUsuarioAjeno() {
        Estudiante alumno = estudiante(2L);
        GrupoEstudio grupo = grupo(TipoGrupo.TUTOR, tutor(1L));
        grupo.getParticipantes().add(alumno);
        assertTrue(reglas.puedePublicarRecursos(grupo, alumno));
        assertFalse(reglas.puedePublicarRecursos(grupo, estudiante(3L)));
        assertFalse(reglas.puedePublicarRecursos(grupo, tutor(4L)));
        assertFalse(reglas.puedeGestionarTemas(grupo, alumno));
    }

    @Test
    void grupoEntreEstudiantesPermiteCompartirYOrganizarTemas() {
        Estudiante creador = estudiante(1L);
        Estudiante alumno = estudiante(2L);
        GrupoEstudio grupo = grupo(TipoGrupo.ESTUDIANTES, creador);
        grupo.getParticipantes().add(creador);
        grupo.getParticipantes().add(alumno);
        grupo.setPermitirPublicaciones(false);
        assertTrue(reglas.publicacionesAbiertas(grupo));
        assertTrue(reglas.puedePublicarRecursos(grupo, alumno));
        assertTrue(reglas.puedeGestionarTemas(grupo, alumno));
        assertFalse(reglas.esAdministrador(grupo, alumno.getIdUsuario()));
        assertEquals(0, reglas.cuposDisponibles(grupo));
    }

    @Test
    void grupoInactivoNoPermiteNuevasPublicacionesNiTemas() {
        Tutor tutor = tutor(1L);
        GrupoEstudio grupo = grupo(TipoGrupo.TUTOR, tutor);
        grupo.setEstado("INACTIVO");
        assertFalse(reglas.puedePublicarRecursos(grupo, tutor));
        assertFalse(reglas.puedeGestionarTemas(grupo, tutor));
    }

    private GrupoEstudio grupo(TipoGrupo tipo, Usuario creador) {
        GrupoEstudio grupo = new GrupoEstudio();
        grupo.setCreador(creador);
        grupo.setTipoGrupo(tipo);
        grupo.setEstado("ACTIVO");
        grupo.setCapacidadMaxima(2);
        grupo.setPermitirPublicaciones(true);
        return grupo;
    }

    private Estudiante estudiante(Long id) {
        Estudiante estudiante = new Estudiante();
        estudiante.setIdUsuario(id);
        return estudiante;
    }

    private Tutor tutor(Long id) {
        Tutor tutor = new Tutor();
        tutor.setIdUsuario(id);
        return tutor;
    }
}
