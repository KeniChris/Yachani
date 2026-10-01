package com.yachaniapi.recursos.controller;

import com.yachaniapi.recursos.dto.request.CompartirMazoRequest;
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
@RequestMapping("/grupos/{idGrupo}/mazos")
@RequiredArgsConstructor
@Validated
public class MazoCompartidoController {

    private final FlashcardsService service;

    @PutMapping("/{idMazo}")
    public OperacionFlashcardsResponse<MazoCompartidoResponse>
    publicar(
            @PathVariable @Positive Long idGrupo,
            @PathVariable @Positive Long idMazo,
            @Valid @RequestBody CompartirMazoRequest request
    ) {
        return service.publicarEnGrupo(
                idGrupo,
                idMazo,
                request
        );
    }

    @GetMapping
    public List<MazoCompartidoResponse> listar(
            @PathVariable @Positive Long idGrupo,
            @RequestParam(required = false)
            @Positive Long idTema
    ) {
        return service.listarGrupo(idGrupo, idTema);
    }

    @GetMapping("/{idMazo}")
    public MazoDetalleResponse consultar(
            @PathVariable @Positive Long idGrupo,
            @PathVariable @Positive Long idMazo
    ) {
        return service.consultarGrupo(idGrupo, idMazo);
    }

    @DeleteMapping("/{idMazo}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void retirar(
            @PathVariable @Positive Long idGrupo,
            @PathVariable @Positive Long idMazo
    ) {
        service.dejarDeCompartir(idGrupo, idMazo);
    }
}