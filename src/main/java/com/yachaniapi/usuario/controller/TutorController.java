package com.yachaniapi.usuario.controller;

import com.yachaniapi.usuario.dto.TutorPerfilRequest;
import com.yachaniapi.usuario.dto.TutorPerfilResponse;
import com.yachaniapi.usuario.service.TutorService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tutores/{idUsuario}/perfil")
public class TutorController {

    private final TutorService tutorService;

    public TutorController(TutorService tutorService) {
        this.tutorService = tutorService;
    }

    /**
     * Consulta la información del perfil del tutor.
     */
    @GetMapping("/datos")
    public ResponseEntity<TutorPerfilResponse> consultarPerfil(
            @PathVariable Long idUsuario) {

        return ResponseEntity.ok(
                tutorService.consultarPerfil(idUsuario)
        );
    }

    /**
     * Actualiza la presentación y el método de enseñanza.
     */
    @PutMapping
    public ResponseEntity<TutorPerfilResponse> actualizarPerfil(
            @PathVariable Long idUsuario,
            @Valid @RequestBody TutorPerfilRequest request) {

        return ResponseEntity.ok(
                tutorService.actualizarPerfil(
                        idUsuario,
                        request
                )
        );
    }
}