package com.yachaniapi.grupoestudio.controller;

import com.yachaniapi.grupoestudio.dto.GrupoEstudioDTO;
import com.yachaniapi.grupoestudio.entity.TipoGrupo;
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

    @GetMapping
    public ResponseEntity<List<GrupoEstudioDTO.Response>> listarGruposDeUsuario(
            @RequestParam Long idUsuario) {
        return ResponseEntity.ok(grupoEstudioService.listarGruposDeUsuario(idUsuario));
    }

    @GetMapping("/{idGrupo}")
    public ResponseEntity<GrupoEstudioDTO.Response> obtenerGrupo(@PathVariable Long idGrupo) {
        return ResponseEntity.ok(grupoEstudioService.obtenerGrupo(idGrupo));
    }

    @PatchMapping("/{idGrupo}/nombre")
    public ResponseEntity<GrupoEstudioDTO.Response> editarNombre(
            @PathVariable Long idGrupo,
            @Valid @RequestBody GrupoEstudioDTO.EditarNombreRequest request) {
        return ResponseEntity.ok(grupoEstudioService.editarNombre(idGrupo, request.getNombre()));
    }

    @PatchMapping("/{idGrupo}/publicaciones")
    public ResponseEntity<GrupoEstudioDTO.Response> cambiarPermitirPublicaciones(
            @PathVariable Long idGrupo,
            @Valid @RequestBody GrupoEstudioDTO.EditarPublicacionesRequest request) {
        return ResponseEntity.ok(
                grupoEstudioService.cambiarPermitirPublicaciones(idGrupo, request.getPermitirPublicaciones()));
    }

    @GetMapping("/{idGrupo}/integrantes")
    public ResponseEntity<List<GrupoEstudioDTO.IntegranteResponse>> listarIntegrantes(
            @PathVariable Long idGrupo) {
        return ResponseEntity.ok(grupoEstudioService.listarIntegrantes(idGrupo));
    }

    @DeleteMapping("/{idGrupo}/integrantes/{idEstudiante}")
    public ResponseEntity<Void> eliminarIntegrante(
            @PathVariable Long idGrupo,
            @PathVariable Long idEstudiante) {
        grupoEstudioService.eliminarIntegrante(idGrupo, idEstudiante);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{idGrupo}/solicitudes")
    public ResponseEntity<List<GrupoEstudioDTO.SolicitudPendienteResponse>> listarSolicitudesPendientes(
            @PathVariable Long idGrupo) {
        return ResponseEntity.ok(grupoEstudioService.listarSolicitudesPendientes(idGrupo));
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

    // Solicitudes que envió un estudiante ("Mis solicitudes").
    @GetMapping("/solicitudes")
    public ResponseEntity<List<GrupoEstudioDTO.MiSolicitudResponse>> listarSolicitudesDeEstudiante(
            @RequestParam Long idEstudiante) {
        return ResponseEntity.ok(grupoEstudioService.listarSolicitudesDeEstudiante(idEstudiante));
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
            @RequestParam(required = false) Integer cantidadIntegrantes,
            @RequestParam(required = false) TipoGrupo tipoGrupo) {
        return ResponseEntity.ok(grupoEstudioService.filtrarGrupos(curso, metodoEstudio, cantidadIntegrantes, tipoGrupo));
    }
}
