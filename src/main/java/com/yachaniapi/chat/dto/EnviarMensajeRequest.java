package com.yachaniapi.chat.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EnviarMensajeRequest {

    private Long idUsuario;

    @NotBlank(message = "El mensaje está vacío")
    @Size(max = 1000, message = "El mensaje no puede superar los 1000 caracteres")
    private String contenido;
}