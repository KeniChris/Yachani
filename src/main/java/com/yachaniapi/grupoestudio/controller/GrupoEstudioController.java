package com.yachaniapi.grupoestudio.controller;

import com.yachaniapi.grupoestudio.dto.request.*;
import com.yachaniapi.grupoestudio.dto.response.*;
import com.yachaniapi.grupoestudio.entity.TipoGrupo;
import com.yachaniapi.grupoestudio.service.GrupoEstudioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/grupos")
@RequiredArgsConstructor
public class GrupoEstudioController {

    private final GrupoEstudioService service;
    private final com.yachaniapi.usuario.service.UsuarioActualService usuarioActual;

    @PostMapping
    public ResponseEntity<GrupoResponse> crearGrupo(@Valid @RequestBody CrearGrupoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crearGrupo(request));
    }

    // Sin idUsuario explora grupos; con idUsuario devuelve sus grupos.
    @GetMapping({"", "/explorar"})
    public ResponseEntity<PaginaResponse<GrupoResponse>> listarGrupos(
            @RequestParam(required = false) Long idUsuario,
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String curso,
            @RequestParam(required = false) String metodoEstudio,
            @RequestParam(required = false) Integer cantidadIntegrantes,
            @RequestParam(required = false) TipoGrupo tipoGrupo,
            @PageableDefault(size = 12, sort = "idGrupo", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(service.listarGrupos(
                idUsuario, query, curso, metodoEstudio, cantidadIntegrantes, tipoGrupo, pageable));
    }

    @GetMapping("/{idGrupo}")
    public ResponseEntity<GrupoResponse> obtenerGrupo(@PathVariable Long idGrupo) {
        return ResponseEntity.ok(service.obtenerGrupo(idGrupo));
    }

    @PatchMapping("/{idGrupo}/nombre")
    public ResponseEntity<GrupoResponse> editarNombre(
            @PathVariable Long idGrupo, @Valid @RequestBody EditarNombreGrupoRequest request) {
        return ResponseEntity.ok(service.editarNombre(idGrupo, request));
    }

    @PatchMapping("/{idGrupo}/capacidad")
    public ResponseEntity<GrupoResponse> actualizarCapacidad(
            @PathVariable Long idGrupo, @Valid @RequestBody EditarCapacidadGrupoRequest request) {
        return ResponseEntity.ok(service.actualizarCapacidad(idGrupo, request));
    }

    @PatchMapping("/{idGrupo}/publicaciones")
    public ResponseEntity<GrupoResponse> cambiarPublicaciones(
            @PathVariable Long idGrupo, @Valid @RequestBody EditarPublicacionesGrupoRequest request) {
        return ResponseEntity.ok(service.cambiarPermitirPublicaciones(idGrupo, request));
    }

    @GetMapping("/{idGrupo}/integrantes")
    public ResponseEntity<List<IntegranteGrupoResponse>> listarIntegrantes(@PathVariable Long idGrupo) {
        return ResponseEntity.ok(service.listarIntegrantes(idGrupo));
    }

    @DeleteMapping("/{idGrupo}/integrantes/{idEstudiante}")
    public ResponseEntity<Void> eliminarIntegrante(
            @PathVariable Long idGrupo, @PathVariable Long idEstudiante) {
        service.eliminarIntegrante(idGrupo, idEstudiante);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{idGrupo}/solicitudes")
    public ResponseEntity<SolicitudGrupoResponse> solicitarUnirse(
            @PathVariable Long idGrupo, @Valid @RequestBody SolicitudGrupoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.solicitarUnirse(idGrupo, request));
    }

    @GetMapping("/{idGrupo}/solicitudes")
    public ResponseEntity<PaginaResponse<SolicitudPendienteResponse>> listarSolicitudesPendientes(
            @PathVariable Long idGrupo,
            @PageableDefault(size = 12, sort = "fechaSolicitud", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(service.listarSolicitudesPendientes(idGrupo, pageable));
    }

    @GetMapping("/solicitudes")
    public ResponseEntity<PaginaResponse<SolicitudPropiaResponse>> listarMisSolicitudes(
            @RequestParam(required = false) Long idEstudiante,
            @PageableDefault(size = 12, sort = "fechaSolicitud", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(service.listarMisSolicitudes(idEstudiante, pageable));
    }

    // PUT conserva la ruta actual; PATCH expresa la actualización parcial.
    @RequestMapping(value = "/solicitudes/{idSolicitud}/aceptar",
            method = {RequestMethod.PUT, RequestMethod.PATCH})
    public ResponseEntity<SolicitudGrupoResponse> aceptarSolicitud(@PathVariable Long idSolicitud) {
        return ResponseEntity.ok(service.aceptarSolicitud(idSolicitud));
    }

    @RequestMapping(value = "/solicitudes/{idSolicitud}/rechazar",
            method = {RequestMethod.PUT, RequestMethod.PATCH})
    public ResponseEntity<SolicitudGrupoResponse> rechazarSolicitud(@PathVariable Long idSolicitud) {
        return ResponseEntity.ok(service.rechazarSolicitud(idSolicitud));
    }

    // Alias conservados para las consultas existentes.
    @GetMapping("/buscar")
    public ResponseEntity<PaginaResponse<GrupoResponse>> buscarGrupos(
            @RequestParam String query,
            @PageableDefault(size = 12, sort = "idGrupo", direction = Sort.Direction.DESC) Pageable pageable) {
        if (query.isBlank()) throw new IllegalArgumentException("Ingresa un curso o tema");
        return ResponseEntity.ok(service.listarGrupos(null, query, null, null, null, null, pageable));
    }

    @GetMapping("/filtrar")
    public ResponseEntity<PaginaResponse<GrupoResponse>> filtrarGrupos(
            @RequestParam(required = false) String curso,
            @RequestParam(required = false) String metodoEstudio,
            @RequestParam(required = false) Integer cantidadIntegrantes,
            @RequestParam(required = false) TipoGrupo tipoGrupo,
            @PageableDefault(size = 12, sort = "idGrupo", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(service.listarGrupos(
                null, null, curso, metodoEstudio, cantidadIntegrantes, tipoGrupo, pageable));
    }

    @GetMapping("/mis-grupos")
    public ResponseEntity<PaginaResponse<GrupoResponse>> listarMisGrupos(
            @PageableDefault(size = 12, sort = "idGrupo", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(service.listarGrupos(
                usuarioActual.idUsuario(), null, null, null, null, null, pageable));
    }
}
