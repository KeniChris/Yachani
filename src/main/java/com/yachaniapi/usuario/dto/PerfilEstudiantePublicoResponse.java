package com.yachaniapi.usuario.dto;
import java.util.List;

public record PerfilEstudiantePublicoResponse(
        Long idUsuario, String nombres, String apellidos,
        String universidad, String carrera,
        List<ConocimientoAcademicoResponse> conocimientos
) {}
