package com.yachaniapi.grupoestudio.controller;

import com.yachaniapi.grupoestudio.dto.GrupoEstudioDTO;
import com.yachaniapi.grupoestudio.service.GrupoEstudioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/grupos")
public class GrupoEstudioController {

    private final GrupoEstudioService grupoEstudioService;

    public GrupoEstudioController(GrupoEstudioService grupoEstudioService) {
        this.grupoEstudioService = grupoEstudioService;
    }

    @PostMapping
    public ResponseEntity<GrupoEstudioDTO.Response> crearGrupo(
            @Valid @RequestBody GrupoEstudioDTO.CrearRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(grupoEstudioService.crearGrupo(request));
    }

    @PatchMapping("/{idGrupo}/capacidad")
    public ResponseEntity<GrupoEstudioDTO.Response> actualizarCapacidad(
            @PathVariable Long idGrupo,
            @Valid @RequestBody GrupoEstudioDTO.EditarCapacidadRequest request) {
        return ResponseEntity.ok(grupoEstudioService.actualizarCapacidad(idGrupo, request));
    }

    @PostMapping("/{idGrupo}/solicitudes")
    public ResponseEntity<GrupoEstudioDTO.SolicitudResponse> solicitarUnirse(
            @PathVariable Long idGrupo,
            @Valid @RequestBody GrupoEstudioDTO.SolicitudRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(grupoEstudioService.solicitarUnirse(idGrupo, request));
    }

    @PutMapping("/solicitudes/{idSolicitud}/aceptar")
    public ResponseEntity<GrupoEstudioDTO.SolicitudResponse> aceptarSolicitud(@PathVariable Long idSolicitud) {
        return ResponseEntity.ok(grupoEstudioService.aceptarSolicitud(idSolicitud));
    }

    @PutMapping("/solicitudes/{idSolicitud}/rechazar")
    public ResponseEntity<GrupoEstudioDTO.SolicitudResponse> rechazarSolicitud(@PathVariable Long idSolicitud) {
        return ResponseEntity.ok(grupoEstudioService.rechazarSolicitud(idSolicitud));
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<GrupoEstudioDTO.Response>> buscarGrupos(@RequestParam(name = "query") String keyword) {
        return ResponseEntity.ok(grupoEstudioService.buscarGrupos(keyword));
    }

    @GetMapping("/filtrar")
    public ResponseEntity<List<GrupoEstudioDTO.Response>> filtrarGrupos(
            @RequestParam(required = false) String curso,
            @RequestParam(required = false) String metodoEstudio,
            @RequestParam(required = false) Integer cantidadIntegrantes) {
        return ResponseEntity.ok(grupoEstudioService.filtrarGrupos(curso, metodoEstudio, cantidadIntegrantes));
    }
}
