package com.yachaniapi.usuario.service;

import com.yachaniapi.usuario.exception.EnvioCorreoException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class CorreoService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String correoRemitente;

    public CorreoService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Envía al usuario el código para recuperar su contraseña.
     */
    public void enviarCodigoRecuperacion(
            String correoDestino,
            String codigo) {

        try {
            SimpleMailMessage mensaje = new SimpleMailMessage();

            mensaje.setFrom(correoRemitente);
            mensaje.setTo(correoDestino);
            mensaje.setSubject(
                    "Código para recuperar tu contraseña - Yachani"
            );
            mensaje.setText(
                    "Hola,\n\n"
                            + "Recibimos una solicitud para cambiar "
                            + "la contraseña de tu cuenta en Yachani.\n\n"
                            + "Tu código de recuperación es: "
                            + codigo
                            + "\n\nEste código será válido durante 10 minutos."
                            + "\n\nSi no solicitaste este cambio, "
                            + "puedes ignorar este mensaje."
                            + "\n\nEquipo Yachani"
            );

            mailSender.send(mensaje);

        } catch (MailException exception) {
            throw new EnvioCorreoException(
                    "No fue posible enviar el código de recuperación"
            );
        }
    }
}