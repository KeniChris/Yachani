package com.yachaniapi.recursos.service;

import com.yachaniapi.grupoestudio.entity.GrupoEstudio;
import com.yachaniapi.recursos.entity.*;
import com.yachaniapi.recursos.exception.FlashcardsException;
import com.yachaniapi.recursos.mapper.FlashcardsMapper;
import com.yachaniapi.recursos.repository.*;
import com.yachaniapi.usuario.entity.Estudiante;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PublicacionesMazoTest {
    @Mock MazoFlashcardsRepository mazos;
    @Mock TarjetaFlashcardRepository tarjetas;
    @Mock MazoCompartidoRepository compartidos;
    @Mock ProgresoTarjetaRepository progresos;
    @Mock TemaGrupoRepository temas;
    @Mock AccesoFlashcardsService acceso;
    @Mock AccesoRecursosService accesoGrupos;
    private FlashcardsService service;
    @BeforeEach void preparar() {
        service = new FlashcardsService(mazos, tarjetas, compartidos, progresos, temas, acceso, accesoGrupos, new FlashcardsMapper());
    }
    @Test void exigePropiedadAntesDeConsultarPublicaciones() {
        when(acceso.propio(10L, false)).thenThrow(new FlashcardsException(HttpStatus.FORBIDDEN, "Solo el autor"));
        assertThrows(FlashcardsException.class, () -> service.listarPublicacionesPropias(10L));
        verifyNoInteractions(compartidos, tarjetas);
    }
    @Test void unBorradorNoTienePublicacionesActivas() {
        var mazo = mazo(); mazo.setEstado(EstadoMazo.BORRADOR);
        when(acceso.propio(10L, false)).thenReturn(mazo);
        assertTrue(service.listarPublicacionesPropias(10L).isEmpty());
        verifyNoInteractions(compartidos, tarjetas);
    }
    @Test void incluyePublicacionesDeGruposALosQueElAutorYaNoPertenece() {
        var mazo = mazo();
        when(acceso.propio(10L, false)).thenReturn(mazo);
        when(tarjetas.countByMazo_IdMazoAndActivaTrue(10L)).thenReturn(2L);
        when(compartidos.findByMazo_IdMazo(10L)).thenReturn(List.of(publicacion(mazo, 20L), publicacion(mazo, 30L)));
        var respuesta = service.listarPublicacionesPropias(10L);
        assertEquals(List.of(20L, 30L), respuesta.stream().map(r -> r.idGrupo()).toList());
        assertEquals(2, respuesta.getFirst().mazo().cantidadTarjetas());
        assertEquals("Derivadas", respuesta.getFirst().nombreTema());
        verifyNoInteractions(accesoGrupos);
    }
    private MazoFlashcards mazo() {
        var autor = new Estudiante(); autor.setIdUsuario(1L); autor.setNombres("Ana"); autor.setApellidos("Pérez");
        var mazo = new MazoFlashcards(); mazo.setIdMazo(10L); mazo.setCreador(autor); mazo.setTitulo("Cálculo"); mazo.setEstado(EstadoMazo.PUBLICADO); return mazo;
    }
    private MazoCompartido publicacion(MazoFlashcards mazo, Long idGrupo) {
        var grupo = new GrupoEstudio(); grupo.setIdGrupo(idGrupo);
        var tema = new TemaGrupo(); tema.setIdTema(idGrupo + 1); tema.setNombre("Derivadas");
        var registro = new MazoCompartido(); registro.setIdCompartido(idGrupo); registro.setMazo(mazo); registro.setGrupo(grupo); registro.setTema(tema); return registro;
    }
}
