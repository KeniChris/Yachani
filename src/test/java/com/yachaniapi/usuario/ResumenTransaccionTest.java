package com.yachaniapi.usuario;

import com.yachaniapi.usuario.entity.*;
import com.yachaniapi.usuario.exception.ArchivoResumenInvalidoException;
import com.yachaniapi.usuario.mapper.ResumenMapper;
import com.yachaniapi.usuario.repository.*;
import com.yachaniapi.usuario.service.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.support.*;
import java.nio.file.*;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResumenTransaccionTest {
    @TempDir Path carpeta;
    @Mock ResumenRepository resumenes;
    @Mock UsuarioRepository usuarios;
    @Mock UsuarioActualService actual;
    ResumenService service;
    Estudiante estudiante;

    @BeforeEach
    void preparar() {
        estudiante = new Estudiante();
        estudiante.setIdUsuario(7L);
        service = new ResumenService(resumenes, usuarios, new ResumenMapper(), actual, carpeta.toString());
        TransactionSynchronizationManager.initSynchronization();
    }

    @AfterEach
    void limpiarSincronizacion() {
        TransactionSynchronizationManager.clearSynchronization();
    }

    @Test
    void unaPublicacionRevertidaLimpiaElArchivoNuevo() throws Exception {
        when(usuarios.findById(7L)).thenReturn(Optional.of(estudiante));
        when(resumenes.save(any())).thenAnswer(i -> i.getArgument(0));
        service.publicarResumen(7L, "Resumen", pdf());
        assertEquals(1, cantidadArchivos());
        completar(TransactionSynchronization.STATUS_ROLLED_BACK);
        assertEquals(0, cantidadArchivos());
    }

    @Test
    void reemplazarYRevertirConservaElArchivoAnterior() throws Exception {
        Path anterior = Files.writeString(carpeta.resolve("anterior.pdf"), "anterior");
        prepararReemplazo(anterior);
        service.reemplazarResumen(7L, 10L, "Nuevo", pdf());
        assertEquals(2, cantidadArchivos());
        completar(TransactionSynchronization.STATUS_ROLLED_BACK);
        assertTrue(Files.exists(anterior));
        assertEquals(1, cantidadArchivos());
    }

    @Test
    void reemplazarEliminaElAnteriorSoloDespuesDelCommit() throws Exception {
        Path anterior = Files.writeString(carpeta.resolve("anterior.pdf"), "anterior");
        prepararReemplazo(anterior);
        service.reemplazarResumen(7L, 10L, "Nuevo", pdf());
        assertTrue(Files.exists(anterior));
        TransactionSynchronizationManager.getSynchronizations().forEach(TransactionSynchronization::afterCommit);
        completar(TransactionSynchronization.STATUS_COMMITTED);
        assertFalse(Files.exists(anterior));
        assertEquals(1, cantidadArchivos());
    }

    @Test
    void elLimiteDeUnMBSeInformaSinGuardarUnArchivo() throws Exception {
        when(usuarios.findById(7L)).thenReturn(Optional.of(estudiante));
        var grande = new MockMultipartFile("archivo", "resumen.pdf", "application/pdf", new byte[1024 * 1024 + 1]);
        var error = assertThrows(ArchivoResumenInvalidoException.class,
                () -> service.publicarResumen(7L, "Resumen", grande));
        assertEquals("El archivo no puede superar 1 MB", error.getMessage());
        verifyNoInteractions(resumenes);
        assertEquals(0, cantidadArchivos());
    }

    @Test
    void noPermitePublicarEnElPerfilDeOtroUsuario() throws Exception {
        when(actual.exigirPropio(8L)).thenThrow(new AccessDeniedException("Perfil ajeno"));
        assertThrows(AccessDeniedException.class, () -> service.publicarResumen(8L, "Resumen", pdf()));
        verifyNoInteractions(usuarios, resumenes);
        assertEquals(0, cantidadArchivos());
    }

    private void prepararReemplazo(Path anterior) {
        when(usuarios.findById(7L)).thenReturn(Optional.of(estudiante));
        var resumen = new Resumen();
        resumen.setIdResumen(10L);
        resumen.setEstudiante(estudiante);
        resumen.setRutaArchivo(anterior.toString());
        when(resumenes.findByIdResumenAndEstudiante_IdUsuario(10L, 7L)).thenReturn(Optional.of(resumen));
        when(resumenes.save(any())).thenAnswer(i -> i.getArgument(0));
    }

    private MockMultipartFile pdf() {
        return new MockMultipartFile("archivo", "resumen.pdf", "application/pdf", "%PDF-ejemplo".getBytes());
    }

    private long cantidadArchivos() throws Exception {
        try (var archivos = Files.list(carpeta)) { return archivos.count(); }
    }

    private void completar(int estado) {
        TransactionSynchronizationManager.getSynchronizations().forEach(s -> s.afterCompletion(estado));
    }
}
