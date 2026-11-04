package com.yachaniapi.aviso.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

public class AvisoDTO {

    @Getter
    @Setter
    @NoArgsConstructor
    public static class CrearRequest {
        private Long idAutor;

        @NotBlank(message = "El título es obligatorio")
        @Size(max = 100, message = "El título no puede superar los 100 caracteres")
        private String titulo;

        @NotBlank(message = "El mensaje es obligatorio")
        @Size(max = 500, message = "El mensaje no puede superar los 500 caracteres")
        private String mensaje;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class EditarRequest {
        private Long idAutor;

        @NotBlank(message = "El título es obligatorio")
        @Size(max = 100, message = "El título no puede superar los 100 caracteres")
        private String titulo;

        @NotBlank(message = "El mensaje es obligatorio")
        @Size(max = 500, message = "El mensaje no puede superar los 500 caracteres")
        private String mensaje;
    }

    @Getter
    @AllArgsConstructor
    public static class Response {
        private Long idAviso;
        private Long idGrupo;
        private Long idAutor;
        private String nombreAutor;
        private String titulo;
        private String mensaje;
        private LocalDateTime fechaPublicacion;
    }
}
