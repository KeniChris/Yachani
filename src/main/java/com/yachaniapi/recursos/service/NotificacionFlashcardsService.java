package com.yachaniapi.recursos.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

@Service
public class NotificacionFlashcardsService {

    private static final Logger log =
            LoggerFactory.getLogger(
                    NotificacionFlashcardsService.class
            );

    private final ObjectProvider<JavaMailSender> mailSender;
    private final String remitente;

    public NotificacionFlashcardsService(
            ObjectProvider<JavaMailSender> mailSender,
            @Value("${spring.mail.username:}") String remitente
    ) {
        this.mailSender = mailSender;
        this.remitente = remitente;
    }

    @TransactionalEventListener(
            phase = TransactionPhase.AFTER_COMMIT
    )
    public void notificar(Publicacion evento) {
        var sender = mailSender.getIfAvailable();

        if (sender == null || remitente.isBlank()) {
            log.warn(
                    "Correo sin configurar: no se envió la notificación de flashcards"
            );
            return;
        }

        for (String destinatario : evento.correos()) {
            try {
                var correo = new SimpleMailMessage();

                correo.setFrom(remitente);
                correo.setTo(destinatario);
                correo.setSubject(
                        "Nuevas flashcards en " + evento.grupo()
                );

                correo.setText(
                        "Se publicó el mazo: " + evento.titulo()
                                + "\nTema: " + evento.tema()
                                + "\nDisponible en el grupo: " + evento.grupo()
                );

                sender.send(correo);

            } catch (Exception exception) {
                log.error(
                        "No se pudo enviar la notificación de flashcards",
                        exception
                );
            }
        }
    }

    public record Publicacion(
            String titulo,
            String grupo,
            String tema,
            List<String> correos
    ) {
    }
}