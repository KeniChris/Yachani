package com.yachaniapi.aviso.controller;

import com.yachaniapi.aviso.dto.AvisoDTO;
import com.yachaniapi.aviso.service.AvisoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/grupos/{idGrupo}/avisos")
public class AvisoController {

    private final AvisoService avisoService;

    public AvisoController(AvisoService avisoService) {
        this.avisoService = avisoService;
    }

    @PostMapping
    public ResponseEntity<AvisoDTO.Response> publicarAviso(
            @PathVariable Long idGrupo,
            @Valid @RequestBody AvisoDTO.CrearRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(avisoService.publicarAviso(idGrupo, request));
    }

    @GetMapping
    public ResponseEntity<List<AvisoDTO.Response>> listarAvisos(@PathVariable Long idGrupo) {
        return ResponseEntity.ok(avisoService.listarAvisos(idGrupo));
    }

    @PutMapping("/{idAviso}")
    public ResponseEntity<AvisoDTO.Response> editarAviso(
            @PathVariable Long idGrupo,
            @PathVariable Long idAviso,
            @Valid @RequestBody AvisoDTO.EditarRequest request) {
        return ResponseEntity.ok(avisoService.editarAviso(idGrupo, idAviso, request));
    }
}
