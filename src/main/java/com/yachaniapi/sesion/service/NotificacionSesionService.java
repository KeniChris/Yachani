package com.yachaniapi.sesion.service;

import com.yachaniapi.sesion.entity.SesionEstudio;
import com.yachaniapi.usuario.entity.Estudiante;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
public class NotificacionSesionService {

    private static final Logger log = LoggerFactory.getLogger(NotificacionSesionService.class);
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FORMATO_HORA = DateTimeFormatter.ofPattern("HH:mm");

    private final JavaMailSender mailSender;
    private final String remitente;

    public NotificacionSesionService(
            JavaMailSender mailSender,
            @Value("${spring.mail.username:}") String remitente) {

        this.mailSender = mailSender;
        this.remitente = remitente;
    }

    public void enviarRecordatorio(SesionEstudio sesion) {
        enviarATodos(
                sesion,
                "Recordatorio: sesión de " + sesion.getTema(),
                "Te recordamos que tienes una sesión de estudio próxima."
        );
    }

    public void notificarCambio(SesionEstudio sesion) {
        enviarATodos(
                sesion,
                "Cambio en la sesión de " + sesion.getTema(),
                "El tutor modificó una sesión de estudio. Estos son los nuevos datos."
        );
    }

    private void enviarATodos(SesionEstudio sesion, String asunto, String introduccion) {

        if (remitente.isBlank()) {
            log.warn("No hay correo configurado, no se enviaron notificaciones");
            return;
        }

        for (Estudiante estudiante : sesion.getGrupo().getParticipantes()) {

            SimpleMailMessage correo = new SimpleMailMessage();
            correo.setFrom(remitente);
            correo.setTo(estudiante.getCorreo());
            correo.setSubject(asunto);
            correo.setText(armarCuerpo(estudiante, sesion, introduccion));

            try {
                mailSender.send(correo);
            } catch (MailException e) {
                log.error("No se pudo enviar el correo a {}", estudiante.getCorreo(), e);
            }
        }
    }

    private String armarCuerpo(Estudiante estudiante, SesionEstudio sesion, String introduccion) {
        return "Hola " + estudiante.getNombres() + ",\n\n"
                + introduccion + "\n\n"
                + "Grupo: " + sesion.getGrupo().getNombre() + "\n"
                + "Tema: " + sesion.getTema() + "\n"
                + "Fecha: " + sesion.getFechaHoraInicio().format(FORMATO_FECHA) + "\n"
                + "Hora: " + sesion.getFechaHoraInicio().format(FORMATO_HORA) + "\n"
                + "Lugar: " + sesion.getLugar() + "\n\n"
                + "Equipo Yachani";
    }
}