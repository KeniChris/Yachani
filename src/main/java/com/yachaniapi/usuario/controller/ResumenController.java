package com.yachaniapi.usuario.controller;

import com.yachaniapi.usuario.dto.ArchivoResumenResponse;
import com.yachaniapi.usuario.dto.ResumenResponse;
import com.yachaniapi.usuario.service.ResumenService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/estudiantes/{idUsuario}/resumenes")
public class ResumenController {

    private final ResumenService resumenService;

    public ResumenController(ResumenService resumenService) {
        this.resumenService = resumenService;
    }

    /**
     * Publica un resumen en el perfil del estudiante
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResumenResponse> publicarResumen(
            @PathVariable Long idUsuario,
            @RequestParam String titulo,
            @RequestPart("archivo") MultipartFile archivo) {

        ResumenResponse respuesta =
                resumenService.publicarResumen(
                        idUsuario,
                        titulo,
                        archivo
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(respuesta);
    }

    /**
     * Muestra los resúmenes publicados en el perfil.
     */
    @GetMapping
    public ResponseEntity<List<ResumenResponse>> listarResumenes(
            @PathVariable Long idUsuario) {

        return ResponseEntity.ok(
                resumenService.listarResumenes(idUsuario)
        );
    }

    /**
     * Reemplaza un resumen publicado por el estudiante.
     */
    @PutMapping(
            value = "/{idResumen}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<ResumenResponse> reemplazarResumen(
            @PathVariable Long idUsuario,
            @PathVariable Long idResumen,
            @RequestParam String titulo,
            @RequestPart("archivo") MultipartFile archivo) {

        return ResponseEntity.ok(
                resumenService.reemplazarResumen(
                        idUsuario,
                        idResumen,
                        titulo,
                        archivo
                )
        );
    }

    /**
     * Permite ver o descargar el archivo de un resumen.
     */
    @GetMapping("/{idResumen}/archivo")
    public ResponseEntity<Resource> descargarResumen(
            @PathVariable Long idUsuario,
            @PathVariable Long idResumen) {

        ArchivoResumenResponse respuesta =
                resumenService.descargarResumen(
                        idUsuario,
                        idResumen
                );

        MediaType tipoContenido;

        try {
            tipoContenido = MediaType.parseMediaType(
                    respuesta.getTipoArchivo()
            );
        } catch (Exception exception) {
            tipoContenido = MediaType.APPLICATION_OCTET_STREAM;
        }

        return ResponseEntity.ok()
                .contentType(tipoContenido)
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" +
                                respuesta.getNombreArchivo() + "\""
                )
                .body(respuesta.getArchivo());
    }
}