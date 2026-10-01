package com.yachaniapi.chat.mapper;

import com.yachaniapi.chat.dto.MensajeChatResponse;
import com.yachaniapi.chat.entity.MensajeChat;
import org.springframework.stereotype.Component;

@Component
public class MensajeChatMapper {

    public MensajeChatResponse toMensajeResponse(MensajeChat mensaje) {

        String nombreAutor = mensaje.getAutor().getNombres()
                + " " + mensaje.getAutor().getApellidos();

        return new MensajeChatResponse(
                mensaje.getIdMensaje(),
                mensaje.getGrupo().getIdGrupo(),
                mensaje.getAutor().getIdUsuario(),
                nombreAutor,
                mensaje.getContenido(),
                mensaje.getFechaEnvio()
        );
    }
}