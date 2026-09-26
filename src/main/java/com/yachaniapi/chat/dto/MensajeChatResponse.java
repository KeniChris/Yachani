package com.yachaniapi.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MensajeChatResponse {

    private Long idMensaje;
    private Long idGrupo;
    private Long idAutor;
    private String nombreAutor;
    private String contenido;
    private LocalDateTime fechaEnvio;
}