package com.yachaniapi.recursos.controller;

import com.yachaniapi.recursos.dto.request.*;
import com.yachaniapi.recursos.dto.response.*;
import com.yachaniapi.recursos.service.FlashcardsService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/mazos/{idMazo}")
@RequiredArgsConstructor
@Validated
public class TarjetaFlashcardController {

    private final FlashcardsService service;

    @PostMapping("/tarjetas")
    @ResponseStatus(HttpStatus.CREATED)
    public OperacionFlashcardsResponse<TarjetaResponse> agregar(
            @PathVariable @Positive Long idMazo,
            @Valid @RequestBody TarjetaRequest request
    ) {
        return service.agregarTarjeta(idMazo, request);
    }

    @PutMapping("/tarjetas/{idTarjeta}")
    public OperacionFlashcardsResponse<TarjetaResponse> editar(
            @PathVariable @Positive Long idMazo,
            @PathVariable @Positive Long idTarjeta,
            @Valid @RequestBody TarjetaRequest request
    ) {
        return service.editarTarjeta(
                idMazo,
                idTarjeta,
                request
        );
    }

    @DeleteMapping("/tarjetas/{idTarjeta}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(
            @PathVariable @Positive Long idMazo,
            @PathVariable @Positive Long idTarjeta
    ) {
        service.eliminarTarjeta(idMazo, idTarjeta);
    }

    @GetMapping("/progreso")
    public ProgresoResponse progreso(
            @PathVariable @Positive Long idMazo
    ) {
        return service.consultarProgreso(idMazo);
    }

    @PutMapping("/tarjetas/{idTarjeta}/progreso")
    public OperacionFlashcardsResponse<ProgresoResponse>
    guardarProgreso(
            @PathVariable @Positive Long idMazo,
            @PathVariable @Positive Long idTarjeta,
            @Valid @RequestBody ProgresoRequest request
    ) {
        return service.guardarProgreso(
                idMazo,
                idTarjeta,
                request
        );
    }
}