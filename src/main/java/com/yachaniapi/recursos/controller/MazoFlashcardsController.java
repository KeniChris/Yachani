package com.yachaniapi.recursos.controller;

import com.yachaniapi.recursos.dto.request.MazoRequest;
import com.yachaniapi.recursos.dto.response.*;
import com.yachaniapi.recursos.service.FlashcardsService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mazos")
@RequiredArgsConstructor
@Validated
public class MazoFlashcardsController {

    private final FlashcardsService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OperacionFlashcardsResponse<MazoResponse> crear(
            @Valid @RequestBody MazoRequest request
    ) {
        return service.crear(request);
    }

    @GetMapping
    public List<MazoResponse> listar() {
        return service.listarPropios();
    }

    @GetMapping("/{idMazo}")
    public MazoDetalleResponse consultar(
            @PathVariable @Positive Long idMazo
    ) {
        return service.consultarPropio(idMazo);
    }

    @PutMapping("/{idMazo}")
    public OperacionFlashcardsResponse<MazoResponse> editar(
            @PathVariable @Positive Long idMazo,
            @Valid @RequestBody MazoRequest request
    ) {
        return service.editar(idMazo, request);
    }

    @DeleteMapping("/{idMazo}/publicacion")
    public OperacionFlashcardsResponse<MazoResponse>
    retirarPublicacion(
            @PathVariable @Positive Long idMazo
    ) {
        return service.retirarPublicacion(idMazo);
    }
}