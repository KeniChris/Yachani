package com.yachaniapi.recursos.service;

import com.yachaniapi.recursos.entity.TemaGrupo;
import com.yachaniapi.recursos.exception.RecursosException;
import com.yachaniapi.recursos.mapper.RecursosMapper;
import com.yachaniapi.recursos.repository.TemaGrupoRepository;
import com.yachaniapi.recursos.dto.request.TemaActualizarRequest;
import com.yachaniapi.recursos.dto.request.TemaRequest;
import com.yachaniapi.recursos.dto.response.TemaResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TemaGrupoService {

    private final TemaGrupoRepository temas;
    private final AccesoRecursosService acceso;
    private final RecursosMapper mapper;
    private final int maxTemas;

    public TemaGrupoService(
            TemaGrupoRepository temas,
            AccesoRecursosService acceso,
            RecursosMapper mapper,
            @Value("${recursos.max-temas:30}") int maxTemas) {

        this.temas = temas;
        this.acceso = acceso;
        this.mapper = mapper;
        this.maxTemas = maxTemas;
    }

    @Transactional
    public TemaResponse crear(
            Long idGrupo,
            TemaRequest request) {

        var contexto = acceso.obtener(idGrupo, true);

        if (temas.countByGrupo_IdGrupo(idGrupo) >= maxTemas) {
            throw new RecursosException(
                    HttpStatus.CONFLICT,
                    "Superó el número de temas: el máximo es " + maxTemas
            );
        }

        TemaGrupo tema = new TemaGrupo();

        tema.setGrupo(contexto.grupo());
        tema.setCreador(contexto.usuario());
        tema.setNombre(request.nombre().trim());
        tema.setDescripcion(
                request.descripcion() == null
                        ? null
                        : request.descripcion().trim()
        );

        return mapper.tema(temas.saveAndFlush(tema));
    }

    @Transactional(readOnly = true)
    public List<TemaResponse> listar(Long idGrupo) {

        acceso.obtener(idGrupo, false);

        return temas.findByGrupo_IdGrupoOrderByNombreAsc(idGrupo)
                .stream()
                .map(mapper::tema)
                .toList();
    }

    @Transactional(readOnly = true)
    public TemaResponse consultar(
            Long idGrupo,
            Long idTema) {

        acceso.obtener(idGrupo, false);

        return mapper.tema(
                buscarEntidad(idGrupo, idTema)
        );
    }

    @Transactional
    public TemaResponse actualizar(
            Long idGrupo,
            Long idTema,
            TemaActualizarRequest request) {

        acceso.obtener(idGrupo, true);

        TemaGrupo tema = buscarEntidad(idGrupo, idTema);

        if (request.nombre() == null
                && request.descripcion() == null) {

            throw new RecursosException(
                    HttpStatus.BAD_REQUEST,
                    "Envía al menos un campo para actualizar"
            );
        }

        if (request.nombre() != null) {

            if (request.nombre().isBlank()) {
                throw new RecursosException(
                        HttpStatus.BAD_REQUEST,
                        "El nombre del tema no puede estar vacío"
                );
            }

            tema.setNombre(request.nombre().trim());
        }

        if (request.descripcion() != null) {
            tema.setDescripcion(
                    request.descripcion().trim()
            );
        }

        return mapper.tema(
                temas.saveAndFlush(tema)
        );
    }

    public TemaGrupo buscarEntidad(
            Long idGrupo,
            Long idTema) {

        return temas.findByIdTemaAndGrupo_IdGrupo(
                        idTema,
                        idGrupo
                )
                .orElseThrow(() ->
                        new RecursosException(
                                HttpStatus.NOT_FOUND,
                                "El tema no existe dentro de este grupo"
                        )
                );
    }
}