package com.yachaniapi.recursos.service;

import com.yachaniapi.grupoestudio.entity.GrupoEstudio;
import com.yachaniapi.recursos.dto.request.TemaRequest;
import com.yachaniapi.recursos.dto.request.TemaActualizarRequest;
import com.yachaniapi.recursos.entity.TemaGrupo;
import com.yachaniapi.recursos.exception.RecursosException;
import com.yachaniapi.recursos.mapper.RecursosMapper;
import com.yachaniapi.recursos.repository.TemaGrupoRepository;
import com.yachaniapi.usuario.entity.Estudiante;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TemaGrupoServiceTest {
    @Mock TemaGrupoRepository repository;
    @Mock AccesoRecursosService acceso;
    private TemaGrupoService service;
    private GrupoEstudio grupo;
    private Estudiante autor;

    @BeforeEach void preparar() {
        service = new TemaGrupoService(repository, acceso, new RecursosMapper(), 30);
        grupo = new GrupoEstudio(); grupo.setIdGrupo(7L);
        autor = new Estudiante(); autor.setIdUsuario(1L);
    }

    @Test void elTrigesimoTemaEstaPermitido() {
        when(acceso.obtenerParaGestionarTemas(7L))
                .thenReturn(new AccesoRecursosService.AccesoGrupo(grupo, autor, true));
        when(repository.countByGrupo_IdGrupo(7L)).thenReturn(29L);
        when(repository.saveAndFlush(any())).thenAnswer(i -> i.getArgument(0));
        var resultado = service.crear(7L, new TemaRequest("  Integrales  ", "Práctica"));
        assertEquals("Integrales", resultado.nombre());
        assertEquals(7L, resultado.idGrupo());
    }

    @Test void elLimiteEsPorGrupoYNoPermiteElTemaTreintaYUno() {
        when(acceso.obtenerParaGestionarTemas(7L))
                .thenReturn(new AccesoRecursosService.AccesoGrupo(grupo, autor, true));
        when(repository.countByGrupo_IdGrupo(7L)).thenReturn(30L);
        RecursosException error = assertThrows(RecursosException.class,
                () -> service.crear(7L, new TemaRequest("Otro tema", null)));
        assertTrue(error.getMessage().contains("30 temas"));
        verify(repository, never()).saveAndFlush(any());
    }

    @Test void permiteEditarElMismoTemaVariasVecesAunqueElGrupoEsteEnElLimite() {
        TemaGrupo tema = new TemaGrupo(); tema.setIdTema(21L);
        tema.setGrupo(grupo); tema.setCreador(autor); tema.setNombre("Inicial");
        when(repository.findByIdTemaAndGrupo_IdGrupo(21L, 7L)).thenReturn(Optional.of(tema));
        when(repository.saveAndFlush(any())).thenAnswer(i -> i.getArgument(0));
        assertEquals("Edición 1", service.actualizar(7L, 21L,
                new TemaActualizarRequest("Edición 1", "Primera")).nombre());
        assertEquals("Edición 2", service.actualizar(7L, 21L,
                new TemaActualizarRequest("Edición 2", "Segunda")).nombre());
        verify(repository, never()).countByGrupo_IdGrupo(any());
        verify(repository, times(2)).saveAndFlush(tema);
    }

    @Test void noConsultaTemasDeUnGrupoAlQueNoPerteneceElUsuario() {
        when(acceso.obtener(7L, false)).thenThrow(
                new RecursosException(HttpStatus.FORBIDDEN, "No perteneces a este grupo"));
        assertThrows(RecursosException.class, () -> service.listar(7L));
        verifyNoInteractions(repository);
    }
}
