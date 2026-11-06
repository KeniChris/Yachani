package com.yachaniapi.aviso.service;

import com.yachaniapi.aviso.dto.AvisoDTO;
import com.yachaniapi.aviso.entity.Aviso;
import com.yachaniapi.aviso.exception.AvisoExceptions.*;
import com.yachaniapi.aviso.mapper.AvisoMapper;
import com.yachaniapi.aviso.repository.AvisoRepository;
import com.yachaniapi.grupoestudio.entity.GrupoEstudio;
import com.yachaniapi.grupoestudio.exception.GrupoEstudioExceptions.GrupoNoEncontradoException;
import com.yachaniapi.grupoestudio.repository.GrupoEstudioRepository;
import com.yachaniapi.usuario.entity.Usuario;
import com.yachaniapi.usuario.exception.UsuarioNoEncontradoException;
import com.yachaniapi.usuario.repository.UsuarioRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AvisoService {

    private final AvisoRepository avisoRepository;
    private final GrupoEstudioRepository grupoEstudioRepository;
    private final UsuarioRepository usuarioRepository;
    private final AvisoMapper avisoMapper;
    private final com.yachaniapi.usuario.service.UsuarioActualService usuarioActual;
    private final com.yachaniapi.grupoestudio.service.AccesoGrupoService acceso;

    public AvisoService(
            AvisoRepository avisoRepository,
            GrupoEstudioRepository grupoEstudioRepository,
            UsuarioRepository usuarioRepository,
            AvisoMapper avisoMapper,
            com.yachaniapi.usuario.service.UsuarioActualService usuarioActual,
            com.yachaniapi.grupoestudio.service.AccesoGrupoService acceso) {
        this.usuarioActual = usuarioActual;
        this.acceso = acceso;

        this.avisoRepository = avisoRepository;
        this.grupoEstudioRepository = grupoEstudioRepository;
        this.usuarioRepository = usuarioRepository;
        this.avisoMapper = avisoMapper;
    }

    @Transactional
    public AvisoDTO.Response publicarAviso(Long idGrupo, AvisoDTO.CrearRequest request) {

        GrupoEstudio grupo = acceso.administrador(idGrupo, true);

        Usuario autor = usuarioActual.exigirPropio(request.getIdAutor());

        validarCreadorDelGrupo(grupo, autor.getIdUsuario());

        Aviso aviso = new Aviso();
        aviso.setGrupo(grupo);
        aviso.setAutor(autor);
        aviso.setTitulo(request.getTitulo().trim());
        aviso.setMensaje(request.getMensaje().trim());

        return avisoMapper.toResponse(avisoRepository.save(aviso));
    }

    @Transactional(readOnly = true)
    public List<AvisoDTO.Response> listarAvisos(Long idGrupo) {
        acceso.miembro(idGrupo, false);
        return avisoRepository.findByGrupo_IdGrupoOrderByFechaPublicacionDesc(idGrupo)
                .stream()
                .map(avisoMapper::toResponse)
                .toList();
    }

    @Transactional
    public AvisoDTO.Response editarAviso(Long idGrupo, Long idAviso, AvisoDTO.EditarRequest request) {

        GrupoEstudio grupo = acceso.administrador(idGrupo, true);
        validarCreadorDelGrupo(grupo, request.getIdAutor());

        Aviso aviso = avisoRepository.findByIdAvisoAndGrupo_IdGrupo(idAviso, idGrupo)
                .orElseThrow(() -> new AvisoNoEncontradoException("El aviso no existe"));

        aviso.setTitulo(request.getTitulo().trim());
        aviso.setMensaje(request.getMensaje().trim());

        return avisoMapper.toResponse(avisoRepository.save(aviso));
    }

    private GrupoEstudio buscarGrupo(Long idGrupo) {
        return grupoEstudioRepository.findById(idGrupo)
                .orElseThrow(() -> new GrupoNoEncontradoException("El grupo de estudio no existe"));
    }

    private void validarCreadorDelGrupo(GrupoEstudio grupo, Long idAutor) {
        usuarioActual.exigirPropio(idAutor);
        if (!grupo.getCreador().getIdUsuario().equals(usuarioActual.idUsuario())) {
            throw new AutorNoAutorizadoException("Solo quien administra el grupo puede publicar avisos");
        }
    }
}
