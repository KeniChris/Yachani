package com.yachaniapi.usuario.service;

import com.yachaniapi.usuario.dto.*;
import com.yachaniapi.usuario.repository.ConocimientoAcademicoRepository;
import com.yachaniapi.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ConfiguracionInicialService {
    private final UsuarioActualService usuarioActual;
    private final UsuarioRepository usuarios;
    private final ConocimientoAcademicoRepository conocimientos;
    private final PerfilAcademicoService perfiles;
    private final TutorService tutores;

    @Transactional
    public PerfilAcademicoResponse guardarEstudiante(ConfiguracionEstudianteRequest request) {
        Long id = usuarioActual.estudianteActual().getIdUsuario();
        usuarios.bloquearPorId(id).orElseThrow();
        Set<String> nombres = new HashSet<>();
        for (var curso : request.conocimientos()) {
            if (!nombres.add(normalizar(curso.getCurso()))) {
                throw new IllegalArgumentException("No repitas un curso en la configuración");
            }
        }
        var perfil = new PerfilAcademicoRequest();
        perfil.setUniversidad(request.universidad());
        perfil.setCarrera(request.carrera());
        perfiles.actualizarPerfilAcademico(id, perfil);

        var existentes = conocimientos.findByEstudiante_IdUsuarioOrderByCursoAsc(id);
        var porNombre = new HashMap<String, Long>();
        for (var curso : existentes) {
            if (nombres.contains(normalizar(curso.getCurso()))) {
                porNombre.put(normalizar(curso.getCurso()), curso.getIdConocimiento());
            } else {
                conocimientos.delete(curso);
            }
        }
        conocimientos.flush();
        for (var curso : request.conocimientos()) {
            Long idCurso = porNombre.get(normalizar(curso.getCurso()));
            if (idCurso == null) perfiles.registrarConocimiento(id, curso);
            else perfiles.actualizarConocimiento(id, idCurso, curso);
        }
        var preferencias = new PreferenciasEstudioRequest();
        preferencias.setModalidadPreferida(request.modalidadPreferida());
        preferencias.setMetodoPreferido(request.metodoPreferido());
        preferencias.setTemasInteres(request.temasInteres());
        return perfiles.actualizarPreferencias(id, preferencias);
    }

    @Transactional
    public TutorPerfilResponse guardarTutor(TutorPerfilRequest request) {
        Long id = usuarioActual.tutorActual().getIdUsuario();
        usuarios.bloquearPorId(id).orElseThrow();
        return tutores.actualizarPerfil(id, request);
    }

    private String normalizar(String texto) {
        return texto.strip().toLowerCase(Locale.ROOT);
    }
}
