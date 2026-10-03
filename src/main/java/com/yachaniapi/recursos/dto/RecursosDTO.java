package com.yachaniapi.recursos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.springframework.data.domain.Page;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

public final class RecursosDTO {

    private RecursosDTO() {}

    public record TemaRequest(
            @NotBlank(message = "El nombre del tema es obligatorio")
            @Size(
                    max = 100,
                    message = "El nombre admite hasta 100 caracteres"
            )
            String nombre,

            @Size(
                    max = 300,
                    message = "La descripción admite hasta 300 caracteres"
            )
            String descripcion
    ) {}

    public record TemaActualizarRequest(
            @Size(
                    min = 1,
                    max = 100,
                    message = "El nombre admite entre 1 y 100 caracteres"
            )
            String nombre,

            @Size(
                    max = 300,
                    message = "La descripción admite hasta 300 caracteres"
            )
            String descripcion
    ) {}

    public record TemaResponse(
            Long idTema,
            Long idGrupo,
            String nombre,
            String descripcion,
            Long idCreador,
            LocalDateTime fechaCreacion
    ) {}

    public record CambiarTemaRequest(
            @Positive(message = "El ID del tema debe ser positivo")
            Long idTema
    ) {}

    public record MaterialResponse(
            Long idMaterial,
            Long idGrupo,
            Long idTema,
            String nombreTema,
            Long idAutor,
            String autor,
            String nombreArchivo,
            String tipoArchivo,
            Long tamanoArchivo,
            LocalDateTime fechaPublicacion
    ) {}

    public record EnvioResponse(
            String mensaje,
            List<MaterialResponse> materiales
    ) {}

    public record DescargaResponse(
            String url,
            Instant venceEn
    ) {}

    public record PaginaResponse<T>(
            List<T> contenido,
            int pagina,
            int tamano,
            long totalElementos,
            int totalPaginas
    ) {
        public static <T> PaginaResponse<T> de(Page<T> page) {
            return new PaginaResponse<>(
                    page.getContent(),
                    page.getNumber(),
                    page.getSize(),
                    page.getTotalElements(),
                    page.getTotalPages()
            );
        }
    }

    public record ErrorResponse(
            LocalDateTime fecha,
            int estado,
            String mensaje,
            List<String> errores
    ) {}
}