package com.yachaniapi.resena.controller;

import com.yachaniapi.resena.dto.PerfilTutorResponse;
import com.yachaniapi.resena.dto.ResenaRequest;
import com.yachaniapi.resena.dto.ResenaResponse;
import com.yachaniapi.resena.service.ResenaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tutores/{idTutor}")
public class ResenaController {

    private final ResenaService resenaService;

    public ResenaController(ResenaService resenaService) {
        this.resenaService = resenaService;
    }

    @GetMapping("/perfil")
    public ResponseEntity<PerfilTutorResponse> consultarPerfilTutor(
            @PathVariable Long idTutor) {

        return ResponseEntity.ok(resenaService.consultarPerfilTutor(idTutor));
    }

    @GetMapping("/resenas")
    public ResponseEntity<List<ResenaResponse>> listarResenas(
            @PathVariable Long idTutor) {

        return ResponseEntity.ok(resenaService.listarResenasRelevantes(idTutor));
    }

    @PostMapping("/resenas")
    public ResponseEntity<ResenaResponse> registrarResena(
            @PathVariable Long idTutor,
            @Valid @RequestBody ResenaRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(resenaService.registrarResena(idTutor, request));
    }
}