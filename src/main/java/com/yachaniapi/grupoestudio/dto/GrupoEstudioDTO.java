package com.yachaniapi.grupoestudio.dto;

import com.yachaniapi.grupoestudio.entity.TipoGrupo;
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

        @NotNull(message = "El ID del usuario creador es obligatorio")
        private Long idCreador;
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
    @Setter
    @NoArgsConstructor
    public static class EditarNombreRequest {
        @NotBlank(message = "El nombre es obligatorio")
        private String nombre;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class EditarPublicacionesRequest {
        @NotNull(message = "Indica si se permiten publicaciones")
        private Boolean permitirPublicaciones;
    }

    @Getter
    @AllArgsConstructor
    public static class IntegranteResponse {
        private Long idUsuario;
        private String nombreCompleto;
        private String correo;
    }

    @Getter
    @AllArgsConstructor
    public static class SolicitudPendienteResponse {
        private Long idSolicitud;
        private Long idGrupo;
        private Long idEstudiante;
        private String nombreEstudiante;
        private String estado;
        private java.time.LocalDateTime fechaSolicitud;
    }

    // Solicitud vista desde el estudiante que la envió: aquí interesa el nombre del
    // grupo, no el del estudiante.
    @Getter
    @AllArgsConstructor
    public static class MiSolicitudResponse {
        private Long idSolicitud;
        private Long idGrupo;
        private String nombreGrupo;
        private String estado;
        private java.time.LocalDateTime fechaSolicitud;
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
        private TipoGrupo tipoGrupo;
        private Long idCreador;
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
