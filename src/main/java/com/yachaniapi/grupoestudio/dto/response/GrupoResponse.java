package com.yachaniapi.grupoestudio.dto.response;

import com.yachaniapi.grupoestudio.entity.TipoGrupo;

public record GrupoResponse(
        Long idGrupo,
        String nombre,
        String descripcion,
        String metodoEstudio,
        Integer capacidadMaxima,
        Integer integrantesActuales,
        String estado,
        TipoGrupo tipoGrupo,
        Long idCreador,
        String nombreCreador,
        String tipoCreador,
        Boolean permitirPublicaciones,
        Integer cuposDisponibles,
        String mensaje
) {}
