package com.yachaniapi.recursos.service;

import com.yachaniapi.recursos.exception.RecursosException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.zip.ZipFile;

@Service
public class ValidadorMaterialService {

    private static final Map<String, String> TIPOS = Map.of(
            "pdf", "application/pdf",
            "docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation",
            "xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            "png", "image/png",
            "jpg", "image/jpeg",
            "jpeg", "image/jpeg"
    );

    private final long maxBytes;
    private final Set<String> extensiones;

    public ValidadorMaterialService(
            @Value("${recursos.max-bytes-archivo:10485760}")
            long maxBytes,

            @Value("${recursos.extensiones:pdf,docx,pptx,xlsx,png,jpg,jpeg}")
            String extensiones) {

        this.maxBytes = maxBytes;
        this.extensiones = Arrays.stream(extensiones.split(","))
                .map(extension ->
                        extension.trim().toLowerCase(Locale.ROOT)
                )
                .collect(Collectors.toUnmodifiableSet());
    }

    public ArchivoValidado validar(MultipartFile archivo) {

        if (archivo == null || archivo.isEmpty()) {
            throw invalido("No se permiten archivos vacíos");
        }

        if (archivo.getSize() > maxBytes) {
            throw new RecursosException(
                    HttpStatus.PAYLOAD_TOO_LARGE,
                    "Cada archivo admite hasta " + maxBytes + " bytes"
            );
        }

        String original = archivo.getOriginalFilename();

        if (original == null) {
            throw invalido("El archivo debe tener un nombre");
        }

        String nombre = original.replace('\\', '/');
        nombre = nombre.substring(nombre.lastIndexOf('/') + 1).trim();

        if (nombre.isBlank()
                || nombre.length() > 255
                || nombre.chars().anyMatch(Character::isISOControl)) {

            throw invalido("El nombre del archivo no es válido");
        }

        int punto = nombre.lastIndexOf('.');

        String extension = punto < 0
                ? ""
                : nombre.substring(punto + 1)
                .toLowerCase(Locale.ROOT);

        if (!extensiones.contains(extension)
                || !TIPOS.containsKey(extension)) {

            throw invalido("El archivo es incompatible");
        }

        try {
            byte[] contenido = archivo.getBytes();

            if (contenido.length == 0) {
                throw invalido("No se permiten archivos vacíos");
            }

            if (contenido.length > maxBytes) {
                throw new RecursosException(
                        HttpStatus.PAYLOAD_TOO_LARGE,
                        "El archivo supera el tamaño permitido"
                );
            }

            if (!contenidoCompatible(contenido, extension)) {
                throw invalido(
                        "El contenido no corresponde a la extensión del archivo"
                );
            }

            return new ArchivoValidado(
                    nombre,
                    extension,
                    TIPOS.get(extension),
                    contenido
            );

        } catch (IOException exception) {
            throw invalido("No se pudo leer el archivo enviado");
        }
    }

    private boolean contenidoCompatible(
            byte[] bytes,
            String extension) throws IOException {

        return switch (extension) {

            case "pdf" -> empiezaCon(
                    bytes,
                    "%PDF-".getBytes(StandardCharsets.US_ASCII)
            );

            case "png" -> empiezaCon(
                    bytes,
                    new byte[]{
                            (byte) 137, 80, 78, 71, 13, 10, 26, 10
                    }
            );

            case "jpg", "jpeg" -> empiezaCon(
                    bytes,
                    new byte[]{
                            (byte) 255, (byte) 216, (byte) 255
                    }
            );

            case "docx", "pptx", "xlsx" ->
                    officeCompatible(bytes, extension);

            default -> false;
        };
    }

    private boolean empiezaCon(byte[] bytes, byte[] firma) {
        return bytes.length >= firma.length
                && Arrays.equals(
                bytes, 0, firma.length,
                firma, 0, firma.length
        );
    }

    private boolean officeCompatible(
            byte[] bytes,
            String extension) throws IOException {

        if (!empiezaCon(bytes, new byte[]{80, 75, 3, 4})) {
            return false;
        }

        String documento = switch (extension) {
            case "docx" -> "word/document.xml";
            case "pptx" -> "ppt/presentation.xml";
            default -> "xl/workbook.xml";
        };

        Path temporal = Files.createTempFile(
                "yachani-validacion-",
                ".zip"
        );

        try {
            Files.write(temporal, bytes);

            try (ZipFile zip = new ZipFile(temporal.toFile())) {
                return zip.getEntry("[Content_Types].xml") != null
                        && zip.getEntry(documento) != null;
            }

        } finally {
            Files.deleteIfExists(temporal);
        }
    }

    private RecursosException invalido(String mensaje) {
        return new RecursosException(
                HttpStatus.BAD_REQUEST,
                mensaje
        );
    }

    public record ArchivoValidado(
            String nombre,
            String extension,
            String tipo,
            byte[] contenido
    ) {}
}