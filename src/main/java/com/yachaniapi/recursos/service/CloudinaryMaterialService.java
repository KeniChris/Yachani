package com.yachaniapi.recursos.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.yachaniapi.recursos.dto.RecursosDTO.DescargaResponse;
import com.yachaniapi.recursos.entity.MaterialEstudio;
import com.yachaniapi.recursos.exception.RecursosException;
import com.yachaniapi.recursos.service.ValidadorMaterialService.ArchivoValidado;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Service
public class CloudinaryMaterialService
        implements AlmacenamientoMaterialService {
    private static final Logger log =
            LoggerFactory.getLogger(CloudinaryMaterialService.class);

    private final Cloudinary cloudinary;
    private final long segundosDescarga;

    public CloudinaryMaterialService(
            @Qualifier("cloudinaryRecursos")
            Cloudinary cloudinary,

            @Value("${recursos.segundos-descarga:120}")
            long segundosDescarga) {

        this.cloudinary = cloudinary;
        this.segundosDescarga = segundosDescarga;
    }

    @Override
    public String nuevoId(Long idGrupo, String extension) {
        return "yachani/grupos/"
                + idGrupo
                + "/"
                + UUID.randomUUID()
                + "."
                + extension;
    }

    @Override
    public void subir(
            String idArchivo,
            ArchivoValidado archivo) {

        verificarConfiguracion();

        try {
            Map<?, ?> resultado = cloudinary.uploader().upload(
                    archivo.contenido(),
                    ObjectUtils.asMap(
                            "public_id", idArchivo,
                            "resource_type", "raw",
                            "type", "authenticated",
                            "overwrite", false,
                            "filename_override", archivo.nombre()
                    )
            );

            if (!idArchivo.equals(resultado.get("public_id"))) {
                throw new IllegalStateException(
                        "Identificador inesperado"
                );
            }

        } catch (Exception e) {
            log.error("Error al subir material a Cloudinary", e);

            throw new RecursosException(
                    HttpStatus.BAD_GATEWAY,
                    "No se pudo subir el archivo al almacenamiento en la nube"
            );
        }
    }

    @Override
    public DescargaResponse descarga(MaterialEstudio material) {

        verificarConfiguracion();

        Instant vence = Instant.now()
                .plusSeconds(segundosDescarga);

        try {
            String url = cloudinary.privateDownload(
                    material.getIdArchivoNube(),
                    null,
                    ObjectUtils.asMap(
                            "resource_type", material.getTipoRecursoNube(),
                            "type", material.getTipoAccesoNube(),
                            "attachment", true,
                            "expires_at", vence.getEpochSecond()
                    )
            );

            return new DescargaResponse(url, vence);

        } catch (Exception exception) {
            throw new RecursosException(
                    HttpStatus.BAD_GATEWAY,
                    "No se pudo generar la descarga del archivo"
            );
        }
    }

    @Override
    public void eliminar(
            String idArchivo,
            String tipoRecurso,
            String tipoAcceso) {

        verificarConfiguracion();

        try {
            Map<?, ?> resultado = cloudinary.uploader().destroy(
                    idArchivo,
                    ObjectUtils.asMap(
                            "resource_type", tipoRecurso,
                            "type", tipoAcceso,
                            "invalidate", true
                    )
            );

            Object estado = resultado.get("result");

            if (!"ok".equals(estado)
                    && !"not found".equals(estado)) {

                throw new IllegalStateException(
                        "Eliminación no confirmada"
                );
            }

        } catch (Exception exception) {
            throw new RecursosException(
                    HttpStatus.BAD_GATEWAY,
                    "No se pudo eliminar el archivo de la nube"
            );
        }
    }

    private void verificarConfiguracion() {
        if (vacio(cloudinary.config.cloudName)
                || vacio(cloudinary.config.apiKey)
                || vacio(cloudinary.config.apiSecret)) {

            throw new RecursosException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "Falta configurar el almacenamiento en la nube"
            );
        }
    }

    private boolean vacio(String valor) {
        return valor == null || valor.isBlank();
    }
}