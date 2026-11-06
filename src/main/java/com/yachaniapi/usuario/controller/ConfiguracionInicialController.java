package com.yachaniapi.usuario.controller;

import com.yachaniapi.usuario.dto.*;
import com.yachaniapi.usuario.service.ConfiguracionInicialService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuarios/me/configuracion")
@RequiredArgsConstructor
public class ConfiguracionInicialController {
    private final ConfiguracionInicialService service;

    @PutMapping("/estudiante")
    @PreAuthorize("hasRole('ESTUDIANTE')")
    public ResponseEntity<PerfilAcademicoResponse> guardarEstudiante(
            @Valid @RequestBody ConfiguracionEstudianteRequest request) {
        return ResponseEntity.ok(service.guardarEstudiante(request));
    }

    @PutMapping("/tutor")
    @PreAuthorize("hasRole('TUTOR')")
    public ResponseEntity<TutorPerfilResponse> guardarTutor(@Valid @RequestBody TutorPerfilRequest request) {
        return ResponseEntity.ok(service.guardarTutor(request));
    }
}
