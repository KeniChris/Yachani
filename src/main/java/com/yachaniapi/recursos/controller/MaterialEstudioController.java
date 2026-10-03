package com.yachaniapi.recursos.controller;

import com.yachaniapi.recursos.service.MaterialEstudioService;
import com.yachaniapi.recursos.dto.request.CambiarTemaRequest;
import com.yachaniapi.recursos.dto.response.DescargaResponse;
import com.yachaniapi.recursos.dto.response.EnvioResponse;
import com.yachaniapi.recursos.dto.response.MaterialResponse;
import com.yachaniapi.recursos.dto.response.PaginaResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.util.List;

@Validated
@RestController
@RequestMapping("/grupos/{idGrupo}/materiales")
public class MaterialEstudioController {

    private final MaterialEstudioService service;

    public MaterialEstudioController(
            MaterialEstudioService service) {

        this.service = service;
    }

    @PostMapping(
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<EnvioResponse> subir(
            @PathVariable @Positive Long idGrupo,
            @RequestParam(required = false) @Positive Long idTema,
            @RequestPart("archivos") List<MultipartFile> archivos) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.subir(
                        idGrupo,
                        idTema,
                        archivos
                ));
    }

    @GetMapping
    public ResponseEntity<PaginaResponse<MaterialResponse>> listar(
            @PathVariable @Positive Long idGrupo,
            @RequestParam(required = false) @Positive Long idTema,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(50) int size) {

        return ResponseEntity.ok(
                PaginaResponse.de(
                        service.listar(
                                idGrupo,
                                idTema,
                                page,
                                size
                        )
                )
        );
    }

    @GetMapping("/{idMaterial}")
    public ResponseEntity<MaterialResponse> consultar(
            @PathVariable @Positive Long idGrupo,
            @PathVariable @Positive Long idMaterial) {

        return ResponseEntity.ok(
                service.consultar(
                        idGrupo,
                        idMaterial
                )
        );
    }

    @PatchMapping("/{idMaterial}/tema")
    public ResponseEntity<MaterialResponse> cambiarTema(
            @PathVariable @Positive Long idGrupo,
            @PathVariable @Positive Long idMaterial,
            @Valid @RequestBody CambiarTemaRequest request) {

        return ResponseEntity.ok(
                service.cambiarTema(
                        idGrupo,
                        idMaterial,
                        request
                )
        );
    }

    @GetMapping("/{idMaterial}/descarga")
    public ResponseEntity<DescargaResponse> descargar(
            @PathVariable @Positive Long idGrupo,
            @PathVariable @Positive Long idMaterial) {

        return ResponseEntity.ok(
                service.descargar(
                        idGrupo,
                        idMaterial
                )
        );
    }

    @DeleteMapping("/{idMaterial}")
    public ResponseEntity<Void> eliminar(
            @PathVariable @Positive Long idGrupo,
            @PathVariable @Positive Long idMaterial) {

        service.eliminar(
                idGrupo,
                idMaterial
        );

        return ResponseEntity.noContent().build();
    }
}