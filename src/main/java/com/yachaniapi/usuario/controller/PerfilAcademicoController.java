package com.yachaniapi.usuario.controller;

import com.yachaniapi.usuario.dto.ConocimientoAcademicoRequest;
import com.yachaniapi.usuario.dto.ConocimientoAcademicoResponse;
import com.yachaniapi.usuario.dto.PerfilAcademicoRequest;
import com.yachaniapi.usuario.dto.PerfilAcademicoResponse;
import com.yachaniapi.usuario.dto.PreferenciasEstudioRequest;
import com.yachaniapi.usuario.service.PerfilAcademicoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/estudiantes/{idUsuario}/perfil")
public class PerfilAcademicoController {

    private final PerfilAcademicoService perfilAcademicoService;

    public PerfilAcademicoController(
            PerfilAcademicoService perfilAcademicoService) {
        this.perfilAcademicoService = perfilAcademicoService;
    }

    /**
     * Consulta toda la información académica del estudiante.
     */
    @GetMapping
    public ResponseEntity<PerfilAcademicoResponse> consultarPerfil(
            @PathVariable Long idUsuario) {

        return ResponseEntity.ok(
                perfilAcademicoService.consultarPerfil(idUsuario)
        );
    }

    /**
     * Actualiza el perfil academico
     */
    @PutMapping("/academico")
    public ResponseEntity<PerfilAcademicoResponse> actualizarPerfil(
            @PathVariable Long idUsuario,
            @Valid @RequestBody PerfilAcademicoRequest request) {

        return ResponseEntity.ok(
                perfilAcademicoService.actualizarPerfilAcademico(
                        idUsuario,
                        request
                )
        );
    }

    /**
     * Actualiza las preferencias de estudio
     */
    @PutMapping("/preferencias")
    public ResponseEntity<PerfilAcademicoResponse> actualizarPreferencias(
            @PathVariable Long idUsuario,
            @Valid @RequestBody PreferenciasEstudioRequest request) {

        return ResponseEntity.ok(
                perfilAcademicoService.actualizarPreferencias(
                        idUsuario,
                        request
                )
        );
    }

    /**
     * Registra un curso y su nivel de conocimiento.
     */
    @PostMapping("/conocimientos")
    public ResponseEntity<ConocimientoAcademicoResponse>
    registrarConocimiento(
            @PathVariable Long idUsuario,
            @Valid @RequestBody ConocimientoAcademicoRequest request) {

        ConocimientoAcademicoResponse respuesta =
                perfilAcademicoService.registrarConocimiento(
                        idUsuario,
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(respuesta);
    }

    /**
     * Actualiza un curso registrado por el estudiante.
     */
    @PutMapping("/conocimientos/{idConocimiento}")
    public ResponseEntity<ConocimientoAcademicoResponse>
    actualizarConocimiento(
            @PathVariable Long idUsuario,
            @PathVariable Long idConocimiento,
            @Valid @RequestBody ConocimientoAcademicoRequest request) {

        return ResponseEntity.ok(
                perfilAcademicoService.actualizarConocimiento(
                        idUsuario,
                        idConocimiento,
                        request
                )
        );
    }
}