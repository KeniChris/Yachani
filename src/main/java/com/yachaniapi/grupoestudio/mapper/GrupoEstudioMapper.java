package com.yachaniapi.grupoestudio.mapper;

import com.yachaniapi.grupoestudio.dto.request.CrearGrupoRequest;
import com.yachaniapi.grupoestudio.dto.response.*;
import com.yachaniapi.grupoestudio.entity.GrupoEstudio;
import com.yachaniapi.grupoestudio.entity.TipoGrupo;
import com.yachaniapi.grupoestudio.entity.SolicitudGrupo;
import com.yachaniapi.usuario.entity.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface GrupoEstudioMapper {

    @Mapping(target = "idGrupo", ignore = true)
    @Mapping(target = "creador", ignore = true)
    @Mapping(target = "participantes", ignore = true)
    @Mapping(target = "estado", ignore = true)
    @Mapping(target = "tipoGrupo", ignore = true)
    @Mapping(target = "permitirPublicaciones", ignore = true)
    GrupoEstudio toEntity(CrearGrupoRequest request);

    @Mapping(target = "idCreador", source = "grupo.creador.idUsuario")
    @Mapping(target = "permitirPublicaciones", source = "publicacionesAbiertas")
    @Mapping(target = "tipoGrupo", source = "tipoGrupo")
    GrupoResponse toResponse(
            GrupoEstudio grupo,
            TipoGrupo tipoGrupo,
            int integrantesActuales,
            int cuposDisponibles,
            boolean publicacionesAbiertas,
            String nombreCreador,
            String tipoCreador,
            String mensaje
    );

    IntegranteGrupoResponse toIntegrante(
            Usuario usuario,
            String nombreCompleto,
            String tipoUsuario,
            boolean administrador
    );

    @Mapping(target = "idGrupo", source = "solicitud.grupo.idGrupo")
    SolicitudGrupoResponse toSolicitud(SolicitudGrupo solicitud, String mensaje);

    @Mapping(target = "idGrupo", source = "solicitud.grupo.idGrupo")
    @Mapping(target = "idEstudiante", source = "solicitud.estudiante.idUsuario")
    SolicitudPendienteResponse toPendiente(
            SolicitudGrupo solicitud, String nombreEstudiante
    );

    @Mapping(target = "idGrupo", source = "grupo.idGrupo")
    @Mapping(target = "nombreGrupo", source = "grupo.nombre")
    SolicitudPropiaResponse toPropia(SolicitudGrupo solicitud);
}
