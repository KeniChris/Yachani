package com.yachaniapi.usuario;

import com.yachaniapi.usuario.exception.EnvioCorreoException;
import com.yachaniapi.usuario.service.CorreoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CorreoServiceTest {
    JavaMailSender sender = mock(JavaMailSender.class);
    CorreoService service = new CorreoService(sender);

    @BeforeEach void configurar() {
        ReflectionTestUtils.setField(service, "correoRemitente", "aplicacion@example.com");
        ReflectionTestUtils.setField(service, "destinatarioContacto", "soporte@example.com");
    }

    @Test void contactoUsaElRemitenteSmtpYRespondeAlVisitante() {
        service.enviarContacto("Ana Pérez", "ana@example.com", "Mi consulta", "Primera línea\nSegunda línea");
        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(sender).send(captor.capture());
        SimpleMailMessage mensaje = captor.getValue();
        assertEquals("aplicacion@example.com", mensaje.getFrom());
        assertArrayEquals(new String[]{"soporte@example.com"}, mensaje.getTo());
        assertEquals("ana@example.com", mensaje.getReplyTo());
        assertEquals("[Contacto Yachani] Mi consulta", mensaje.getSubject());
        assertTrue(mensaje.getText().contains("Ana Pérez"));
        assertTrue(mensaje.getText().contains("Primera línea\nSegunda línea"));
    }

    @Test void falloSmtpSeExponeComoErrorDeEnvio() {
        doThrow(new MailSendException("SMTP no disponible")).when(sender).send(any(SimpleMailMessage.class));
        assertThrows(EnvioCorreoException.class,
                () -> service.enviarContacto("Ana", "ana@example.com", "Consulta", "Ayuda"));
    }

    @Test void noIntentaEnviarSinConfiguracionDeCorreo() {
        ReflectionTestUtils.setField(service, "correoRemitente", "");
        assertThrows(EnvioCorreoException.class,
                () -> service.enviarContacto("Ana", "ana@example.com", "Consulta", "Ayuda"));
        verifyNoInteractions(sender);
    }

    @Test void recuperacionConservaSuDestinoCodigoYDuracion() {
        service.enviarCodigoRecuperacion("cuenta@upc.edu.pe", "123456");
        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(sender).send(captor.capture());
        SimpleMailMessage mensaje = captor.getValue();
        assertArrayEquals(new String[]{"cuenta@upc.edu.pe"}, mensaje.getTo());
        assertNull(mensaje.getReplyTo());
        assertEquals("Código para recuperar tu contraseña - Yachani", mensaje.getSubject());
        assertTrue(mensaje.getText().contains("123456"));
        assertTrue(mensaje.getText().contains("10 minutos"));
    }
}
