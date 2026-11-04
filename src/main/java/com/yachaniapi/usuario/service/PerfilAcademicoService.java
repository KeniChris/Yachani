package com.yachaniapi.usuario.service;

import com.yachaniapi.usuario.dto.ConocimientoAcademicoRequest;
import com.yachaniapi.usuario.dto.ConocimientoAcademicoResponse;
import com.yachaniapi.usuario.dto.PerfilAcademicoRequest;
import com.yachaniapi.usuario.dto.PerfilAcademicoResponse;
import com.yachaniapi.usuario.dto.PreferenciasEstudioRequest;
import com.yachaniapi.usuario.entity.ConocimientoAcademico;
import com.yachaniapi.usuario.entity.Estudiante;
import com.yachaniapi.usuario.entity.Usuario;
import com.yachaniapi.usuario.exception.UsuarioNoEncontradoException;
import com.yachaniapi.usuario.exception.UsuarioNoEsEstudianteException;
import com.yachaniapi.usuario.mapper.PerfilAcademicoMapper;
import com.yachaniapi.usuario.repository.ConocimientoAcademicoRepository;
import com.yachaniapi.usuario.repository.UsuarioRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Gestiona el perfil académico, las preferencias
 * y los cursos registrados por el estudiante.
 */
@Service
public class PerfilAcademicoService {

    private final UsuarioRepository usuarioRepository;
    private final ConocimientoAcademicoRepository conocimientoRepository;
    private final PerfilAcademicoMapper perfilAcademicoMapper;
    private final UsuarioActualService usuarioActual;
    private final EstadoConfiguracionService estadoConfiguracion;

    public PerfilAcademicoService(
            UsuarioRepository usuarioRepository,
            ConocimientoAcademicoRepository conocimientoRepository,
            PerfilAcademicoMapper perfilAcademicoMapper,
            UsuarioActualService usuarioActual,
            EstadoConfiguracionService estadoConfiguracion) {
        this.usuarioActual = usuarioActual;
        this.estadoConfiguracion = estadoConfiguracion;

        this.usuarioRepository = usuarioRepository;
        this.conocimientoRepository = conocimientoRepository;
        this.perfilAcademicoMapper = perfilAcademicoMapper;
    }

    /**
     * Actualiza la universidad y la carrera del estudiante.
     */
    @Transactional
    public PerfilAcademicoResponse actualizarPerfilAcademico(
            Long idUsuario,
            PerfilAcademicoRequest request) {

        Estudiante estudiante = buscarEstudiante(idUsuario);

        estudiante.setUniversidad(
                request.getUniversidad().trim()
        );

        estudiante.setCarrera(
                request.getCarrera().trim()
        );

        estadoConfiguracion.actualizar(estudiante);
        usuarioRepository.save(estudiante);

        return construirRespuesta(estudiante);
    }

    /**
     * Actualiza las preferencias de estudio del estudiante.
     */
    @Transactional
    public PerfilAcademicoResponse actualizarPreferencias(
            Long idUsuario,
            PreferenciasEstudioRequest request) {

        Estudiante estudiante = buscarEstudiante(idUsuario);

        List<String> temasLimpios = request.getTemasInteres()
                .stream()
                .filter(tema -> tema != null && !tema.isBlank())
                .map(String::trim)
                .distinct()
                .toList();

        estudiante.setModalidadPreferida(
                request.getModalidadPreferida().trim()
        );

        estudiante.setMetodoPreferido(
                request.getMetodoPreferido().trim()
        );

        estudiante.setTemasInteres(
                new ArrayList<>(temasLimpios)
        );

        estadoConfiguracion.actualizar(estudiante);
        usuarioRepository.save(estudiante);

        return construirRespuesta(estudiante);
    }

    /**
     * Registra un nuevo curso y su nivel de conocimiento.
     */
    @Transactional
    public ConocimientoAcademicoResponse registrarConocimiento(
            Long idUsuario,
            ConocimientoAcademicoRequest request) {

        Estudiante estudiante = buscarEstudiante(idUsuario);

        String curso = request.getCurso().trim();

        boolean cursoRegistrado = conocimientoRepository
                .existsByEstudiante_IdUsuarioAndCursoIgnoreCase(
                        idUsuario,
                        curso
                );

        if (cursoRegistrado) {
            throw new IllegalArgumentException(
                    "El estudiante ya tiene registrado este curso"
            );
        }

        ConocimientoAcademico conocimiento =
                new ConocimientoAcademico();

        conocimiento.setEstudiante(estudiante);
        conocimiento.setCurso(curso);
        conocimiento.setNivel(request.getNivel().trim());

        ConocimientoAcademico conocimientoGuardado =
                conocimientoRepository.saveAndFlush(conocimiento);
        estadoConfiguracion.actualizar(estudiante);
        usuarioRepository.save(estudiante);

        return perfilAcademicoMapper.toConocimientoResponse(
                conocimientoGuardado
        );
    }

    /**
     * Actualiza un curso que pertenece al estudiante.
     */
    @Transactional
    public ConocimientoAcademicoResponse actualizarConocimiento(
            Long idUsuario,
            Long idConocimiento,
            ConocimientoAcademicoRequest request) {

        Estudiante estudiante = buscarEstudiante(idUsuario);

        ConocimientoAcademico conocimiento =
                conocimientoRepository
                        .findByIdConocimientoAndEstudiante_IdUsuario(
                                idConocimiento,
                                idUsuario
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "El conocimiento académico no existe"
                                )
                        );

        String nuevoCurso = request.getCurso().trim();

        boolean cambioCurso = !conocimiento.getCurso()
                .equalsIgnoreCase(nuevoCurso);

        boolean cursoRegistrado = conocimientoRepository
                .existsByEstudiante_IdUsuarioAndCursoIgnoreCase(
                        idUsuario,
                        nuevoCurso
                );

        if (cambioCurso && cursoRegistrado) {
            throw new IllegalArgumentException(
                    "El estudiante ya tiene registrado este curso"
            );
        }

        conocimiento.setCurso(nuevoCurso);
        conocimiento.setNivel(request.getNivel().trim());

        ConocimientoAcademico conocimientoActualizado =
                conocimientoRepository.saveAndFlush(conocimiento);
        estadoConfiguracion.actualizar(estudiante);
        usuarioRepository.save(estudiante);

        return perfilAcademicoMapper.toConocimientoResponse(
                conocimientoActualizado
        );
    }

    /**
     * Consulta toda la información académica del estudiante.
     */
    @Transactional
    public PerfilAcademicoResponse consultarPerfil(Long idUsuario) {

        Estudiante estudiante = buscarEstudiante(idUsuario);

        return construirRespuesta(estudiante);
    }

    /**
     * Busca al usuario y comprueba que sea estudiante.
     */
    private Estudiante buscarEstudiante(Long idUsuario) {

        usuarioActual.exigirPropio(idUsuario);
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() ->
                        new UsuarioNoEncontradoException(
                                "Usuario no encontrado"
                        )
                );

        if (!(usuario instanceof Estudiante estudiante)) {
            throw new UsuarioNoEsEstudianteException(
                    "El usuario indicado no es un estudiante"
            );
        }

        return estudiante;
    }

    /**
     * Obtiene los cursos y prepara la respuesta completa del perfil.
     */
    private PerfilAcademicoResponse construirRespuesta(
            Estudiante estudiante) {

        List<ConocimientoAcademico> conocimientos =
                conocimientoRepository
                        .findByEstudiante_IdUsuarioOrderByCursoAsc(
                                estudiante.getIdUsuario()
                        );

        return perfilAcademicoMapper.toPerfilResponse(
                estudiante,
                conocimientos
        );
    }

    @Transactional
    public void eliminarConocimiento(Long idUsuario, Long idConocimiento) {
        Estudiante estudiante = buscarEstudiante(idUsuario);
        ConocimientoAcademico curso = conocimientoRepository
                .findByIdConocimientoAndEstudiante_IdUsuario(idConocimiento, idUsuario)
                .orElseThrow(() -> new IllegalArgumentException("El conocimiento académico no existe"));
        conocimientoRepository.delete(curso);
        conocimientoRepository.flush();
        estadoConfiguracion.actualizar(estudiante);
        usuarioRepository.save(estudiante);
    }

    @Transactional(readOnly = true)
    public com.yachaniapi.usuario.dto.PerfilEstudiantePublicoResponse consultarPublico(Long idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new UsuarioNoEncontradoException("Estudiante no encontrado"));
        if (!(usuario instanceof Estudiante estudiante)) {
            throw new UsuarioNoEsEstudianteException("El usuario no es estudiante");
        }
        var cursos = conocimientoRepository.findByEstudiante_IdUsuarioOrderByCursoAsc(idUsuario)
                .stream().map(perfilAcademicoMapper::toConocimientoResponse).toList();
        return new com.yachaniapi.usuario.dto.PerfilEstudiantePublicoResponse(
                idUsuario, estudiante.getNombres(), estudiante.getApellidos(),
                estudiante.getUniversidad(), estudiante.getCarrera(), cursos);
    }
}