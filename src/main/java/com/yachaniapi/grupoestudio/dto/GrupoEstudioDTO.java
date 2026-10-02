package com.yachaniapi.grupoestudio.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class GrupoEstudioDTO {

    @Getter
    @Setter
    @NoArgsConstructor
    public static class CrearRequest {
        @NotBlank(message = "El nombre es obligatorio")
        private String nombre;

        @NotBlank(message = "La descripción es obligatoria")
        private String descripcion;

        @NotBlank(message = "El método de estudio es obligatorio")
        private String metodoEstudio;

        @NotNull(message = "La capacidad máxima es obligatoria")
        @Min(value = 2, message = "El límite mínimo es de 2 estudiantes")
        private Integer capacidadMaxima;

        @NotNull(message = "El ID del tutor creador es obligatorio")
        private Long idTutor;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class EditarCapacidadRequest {
        @NotNull(message = "La nueva capacidad es obligatoria")
        @Min(value = 2, message = "El límite mínimo es de 2 estudiantes")
        private Integer nuevaCapacidad;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class SolicitudRequest {
        @NotNull(message = "El ID del estudiante es obligatorio")
        private Long idEstudiante;
    }

    @Getter
    @AllArgsConstructor
    public static class Response {
        private Long idGrupo;
        private String nombre;
        private String descripcion;
        private String metodoEstudio;
        private Integer capacidadMaxima;
        private Integer integrantesActuales;
        private String estado;
        private String mensaje;
    }

    @Getter
    @AllArgsConstructor
    public static class SolicitudResponse {
        private Long idSolicitud;
        private Long idGrupo;
        private String estado;
        private String mensaje;
    }
}
