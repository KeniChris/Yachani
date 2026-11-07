package com.yachaniapi.contacto.service;

import com.yachaniapi.contacto.dto.ContactoRequest;
import com.yachaniapi.contacto.exception.LimiteContactoException;
import com.yachaniapi.usuario.service.CorreoService;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LimiteContactoTest {
    @Test void limitaCadaDireccionYPermiteReintentarAlVencerLaVentana() {
        Clock reloj = mock(Clock.class);
        Instant inicio = Instant.parse("2026-11-06T12:00:00Z");
        when(reloj.instant()).thenReturn(inicio);
        LimiteContacto limite = new LimiteContacto(reloj);
        IntStream.range(0, 3).forEach(i -> limite.verificar("direccion-a"));
        assertThrows(LimiteContactoException.class, () -> limite.verificar("direccion-a"));
        assertDoesNotThrow(() -> limite.verificar("direccion-b"));
        when(reloj.instant()).thenReturn(inicio.plusSeconds(900));
        assertDoesNotThrow(() -> limite.verificar("direccion-a"));
    }

    @Test void losEnviosConcurrentesRespetanElLimite() {
        LimiteContacto limite = new LimiteContacto();
        AtomicInteger permitidos = new AtomicInteger();
        IntStream.range(0, 20).parallel().forEach(i -> {
            try {
                limite.verificar("misma-direccion");
                permitidos.incrementAndGet();
            } catch (LimiteContactoException ignorada) { }
        });
        assertEquals(3, permitidos.get());
    }

    @Test void unaSolicitudRechazadaNoLlegaAlServicioDeCorreo() {
        CorreoService correo = mock(CorreoService.class);
        ContactoService service = new ContactoService(correo, new LimiteContacto());
        ContactoRequest request = new ContactoRequest("Ana", "ana@example.com", "Ayuda", "Mi consulta");
        IntStream.range(0, 3).forEach(i -> service.enviar(request, "direccion"));
        assertThrows(LimiteContactoException.class, () -> service.enviar(request, "direccion"));
        verify(correo, times(3)).enviarContacto("Ana", "ana@example.com", "Ayuda", "Mi consulta");
    }
}
