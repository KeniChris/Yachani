package com.yachaniapi.sesion.controller;

import com.yachaniapi.sesion.dto.SesionRequest;
import com.yachaniapi.sesion.dto.SesionResponse;
import com.yachaniapi.sesion.service.SesionEstudioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/grupos/{idGrupo}/sesiones")
public class SesionEstudioController {

    private final SesionEstudioService sesionService;

    public SesionEstudioController(SesionEstudioService sesionService) {
        this.sesionService = sesionService;
    }

    @PostMapping
    public ResponseEntity<SesionResponse> crearSesion(
            @PathVariable Long idGrupo,
            @Valid @RequestBody SesionRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(sesionService.crearSesion(idGrupo, request));
    }

    @GetMapping
    public ResponseEntity<List<SesionResponse>> listarSesiones(
            @PathVariable Long idGrupo) {

        return ResponseEntity.ok(sesionService.listarSesiones(idGrupo));
    }

    @PutMapping("/{idSesion}")
    public ResponseEntity<SesionResponse> modificarSesion(
            @PathVariable Long idGrupo,
            @PathVariable Long idSesion,
            @Valid @RequestBody SesionRequest request) {

        return ResponseEntity.ok(sesionService.modificarSesion(idGrupo, idSesion, request));
    }

    @PatchMapping("/{idSesion}/cancelar")
    public ResponseEntity<SesionResponse> cancelarSesion(
            @PathVariable Long idGrupo,
            @PathVariable Long idSesion,
            @RequestParam Long idTutor) {

        return ResponseEntity.ok(sesionService.cancelarSesion(idGrupo, idSesion, idTutor));
    }
}