package com.yachaniapi.recursos.controller;

import com.yachaniapi.recursos.dto.RecursosDTO.*;
import com.yachaniapi.recursos.service.TemaGrupoService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/grupos/{idGrupo}/temas")
public class TemaGrupoController {

    private final TemaGrupoService service;

    public TemaGrupoController(TemaGrupoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<TemaResponse> crear(
            @PathVariable @Positive Long idGrupo,
            @Valid @RequestBody TemaRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.crear(idGrupo, request));
    }

    @GetMapping
    public ResponseEntity<List<TemaResponse>> listar(
            @PathVariable @Positive Long idGrupo) {

        return ResponseEntity.ok(
                service.listar(idGrupo)
        );
    }

    @GetMapping("/{idTema}")
    public ResponseEntity<TemaResponse> consultar(
            @PathVariable @Positive Long idGrupo,
            @PathVariable @Positive Long idTema) {

        return ResponseEntity.ok(
                service.consultar(idGrupo, idTema)
        );
    }

    @PatchMapping("/{idTema}")
    public ResponseEntity<TemaResponse> actualizar(
            @PathVariable @Positive Long idGrupo,
            @PathVariable @Positive Long idTema,
            @Valid @RequestBody TemaActualizarRequest request) {

        return ResponseEntity.ok(
                service.actualizar(
                        idGrupo,
                        idTema,
                        request
                )
        );
    }
}