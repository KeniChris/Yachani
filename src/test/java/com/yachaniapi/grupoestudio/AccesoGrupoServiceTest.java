package com.yachaniapi.grupoestudio;

import com.yachaniapi.grupoestudio.entity.*;
import com.yachaniapi.grupoestudio.repository.GrupoEstudioRepository;
import com.yachaniapi.grupoestudio.service.*;
import com.yachaniapi.usuario.entity.Estudiante;
import com.yachaniapi.usuario.service.UsuarioActualService;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AccesoGrupoServiceTest {
    @Test
    void unEstudianteCreadorTambienEsAdministrador() {
        var repo = mock(GrupoEstudioRepository.class);
        var usuario = mock(UsuarioActualService.class);
        var creador = estudiante(1L);
        GrupoEstudio grupo = grupo(creador);
        when(usuario.idUsuario()).thenReturn(1L);
        when(repo.buscarConBloqueo(10L)).thenReturn(Optional.of(grupo));
        var acceso = new AccesoGrupoService(repo, usuario, new ReglasGrupoService());
        assertSame(grupo, acceso.administrador(10L, true));
    }

    @Test
    void pertenecerNoPermiteAdministrar() {
        var repo = mock(GrupoEstudioRepository.class);
        var usuario = mock(UsuarioActualService.class);
        GrupoEstudio grupo = grupo(estudiante(1L));
        grupo.getParticipantes().add(estudiante(2L));
        when(usuario.idUsuario()).thenReturn(2L);
        when(repo.findById(10L)).thenReturn(Optional.of(grupo));
        var acceso = new AccesoGrupoService(repo, usuario, new ReglasGrupoService());
        assertThrows(AccessDeniedException.class, () -> acceso.administrador(10L, false));
    }

    @Test
    void unUsuarioAjenoNoLeeLosDatosInternosDelGrupo() {
        var repo = mock(GrupoEstudioRepository.class);
        var usuario = mock(UsuarioActualService.class);
        when(usuario.idUsuario()).thenReturn(3L);
        when(repo.findById(10L)).thenReturn(Optional.of(grupo(estudiante(1L))));
        var acceso = new AccesoGrupoService(repo, usuario, new ReglasGrupoService());
        assertThrows(AccessDeniedException.class, () -> acceso.miembro(10L, false));
    }

    private Estudiante estudiante(Long id) {
        var estudiante = new Estudiante(); estudiante.setIdUsuario(id); return estudiante;
    }

    private GrupoEstudio grupo(Estudiante creador) {
        var grupo = new GrupoEstudio();
        grupo.setIdGrupo(10L); grupo.setCreador(creador);
        grupo.setTipoGrupo(TipoGrupo.ESTUDIANTES); grupo.setEstado("ACTIVO");
        grupo.setCapacidadMaxima(10); grupo.getParticipantes().add(creador);
        return grupo;
    }
}
