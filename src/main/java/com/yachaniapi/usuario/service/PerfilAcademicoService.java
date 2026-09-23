package com.yachaniapi.usuario.service;

import com.yachaniapi.usuario.dto.ConocimientoAcademicoRequest;
import com.yachaniapi.usuario.dto.ConocimientoAcademicoResponse;
import com.yachaniapi.usuario.dto.PerfilAcademicoRequest;
import com.yachaniapi.usuario.dto.PerfilAcademicoResponse;
import com.yachaniapi.usuario.dto.PreferenciasEstudioRequest;
import com.yachaniapi.usuario.model.ConocimientoAcademico;
import com.yachaniapi.usuario.model.Estudiante;
import com.yachaniapi.usuario.model.Usuario;
import com.yachaniapi.usuario.repository.ConocimientoAcademicoRepository;
import com.yachaniapi.usuario.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * se gestiona aqui el perfil académico, las preferencias y los cursos registrados por el estudiante.
 */
@Service
public class PerfilAcademicoService {

    private final UsuarioRepository usuarioRepository;
    private final ConocimientoAcademicoRepository conocimientoRepository;

    public PerfilAcademicoService(
            UsuarioRepository usuarioRepository,
            ConocimientoAcademicoRepository conocimientoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.conocimientoRepository = conocimientoRepository;
    }

    /**
     * Actualiza la universidad y la carrera del estudiante.
     */
    @Transactional
    public PerfilAcademicoResponse actualizarPerfilAcademico(
            Long idUsuario,
            PerfilAcademicoRequest request) {

        Estudiante estudiante = buscarEstudiante(idUsuario);

        estudiante.setUniversidad(request.getUniversidad().trim());
        estudiante.setCarrera(request.getCarrera().trim());

        usuarioRepository.save(estudiante);

        return construirRespuesta(estudiante);
    }

    /**
     * Actualiza las preferencias de estudio.
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
        estudiante.setTemasInteres(new ArrayList<>(temasLimpios));

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

        if (conocimientoRepository
                .existsByEstudiante_IdUsuarioAndCursoIgnoreCase(
                        idUsuario,
                        curso)) {
            throw new IllegalArgumentException(
                    "El estudiante ya tiene registrado este curso"
            );
        }

        ConocimientoAcademico conocimiento =
                new ConocimientoAcademico();

        conocimiento.setEstudiante(estudiante);
        conocimiento.setCurso(curso);
        conocimiento.setNivel(request.getNivel().trim());

        conocimientoRepository.save(conocimiento);

        return convertirConocimiento(conocimiento);
    }

    /**
     * Actualiza un curso que pertenece al estudiante.
     */
    @Transactional
    public ConocimientoAcademicoResponse actualizarConocimiento(
            Long idUsuario,
            Long idConocimiento,
            ConocimientoAcademicoRequest request) {

        buscarEstudiante(idUsuario);

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

        if (cambioCurso && conocimientoRepository
                .existsByEstudiante_IdUsuarioAndCursoIgnoreCase(
                        idUsuario,
                        nuevoCurso)) {
            throw new IllegalArgumentException(
                    "El estudiante ya tiene registrado este curso"
            );
        }

        conocimiento.setCurso(nuevoCurso);
        conocimiento.setNivel(request.getNivel().trim());

        conocimientoRepository.save(conocimiento);

        return convertirConocimiento(conocimiento);
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
     * se busca al usuario y comprueba que sea estudiante.
     */
    private Estudiante buscarEstudiante(Long idUsuario) {

        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "El usuario no existe"
                        )
                );

        if (!(usuario instanceof Estudiante estudiante)) {
            throw new IllegalArgumentException(
                    "El usuario indicado no es un estudiante"
            );
        }

        return estudiante;
    }

    /**
     * Construye la respuesta completa del perfil.
     */
    private PerfilAcademicoResponse construirRespuesta(
            Estudiante estudiante) {

        List<ConocimientoAcademicoResponse> conocimientos =
                conocimientoRepository
                        .findByEstudiante_IdUsuarioOrderByCursoAsc(
                                estudiante.getIdUsuario()
                        )
                        .stream()
                        .map(this::convertirConocimiento)
                        .toList();

        return new PerfilAcademicoResponse(
                estudiante.getIdUsuario(),
                estudiante.getNombres(),
                estudiante.getApellidos(),
                estudiante.getCorreo(),
                estudiante.getUniversidad(),
                estudiante.getCarrera(),
                estudiante.getModalidadPreferida(),
                estudiante.getMetodoPreferido(),
                new ArrayList<>(estudiante.getTemasInteres()),
                conocimientos
        );
    }

    /**
     * Toma los datos del conocimiento académico guardado
     * y prepara la información que se devolverá al usuario.
     */
    private ConocimientoAcademicoResponse convertirConocimiento(
            ConocimientoAcademico conocimiento) {

        return new ConocimientoAcademicoResponse(
                conocimiento.getIdConocimiento(),
                conocimiento.getCurso(),
                conocimiento.getNivel()
        );
    }
}