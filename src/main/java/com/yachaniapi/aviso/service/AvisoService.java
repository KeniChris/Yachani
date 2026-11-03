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
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AvisoService {

    private final AvisoRepository avisoRepository;
    private final GrupoEstudioRepository grupoEstudioRepository;
    private final UsuarioRepository usuarioRepository;
    private final AvisoMapper avisoMapper;

    public AvisoService(
            AvisoRepository avisoRepository,
            GrupoEstudioRepository grupoEstudioRepository,
            UsuarioRepository usuarioRepository,
            AvisoMapper avisoMapper) {

        this.avisoRepository = avisoRepository;
        this.grupoEstudioRepository = grupoEstudioRepository;
        this.usuarioRepository = usuarioRepository;
        this.avisoMapper = avisoMapper;
    }

    @Transactional
    public AvisoDTO.Response publicarAviso(Long idGrupo, AvisoDTO.CrearRequest request) {

        GrupoEstudio grupo = buscarGrupo(idGrupo);

        Usuario autor = usuarioRepository.findById(request.getIdAutor())
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado"));

        validarCreadorDelGrupo(grupo, autor.getIdUsuario());

        Aviso aviso = new Aviso();
        aviso.setGrupo(grupo);
        aviso.setAutor(autor);
        aviso.setTitulo(request.getTitulo().trim());
        aviso.setMensaje(request.getMensaje().trim());

        return avisoMapper.toResponse(avisoRepository.save(aviso));
    }

    public List<AvisoDTO.Response> listarAvisos(Long idGrupo) {
        buscarGrupo(idGrupo);
        return avisoRepository.findByGrupo_IdGrupoOrderByFechaPublicacionDesc(idGrupo)
                .stream()
                .map(avisoMapper::toResponse)
                .toList();
    }

    @Transactional
    public AvisoDTO.Response editarAviso(Long idGrupo, Long idAviso, AvisoDTO.EditarRequest request) {

        GrupoEstudio grupo = buscarGrupo(idGrupo);
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
        if (!grupo.getCreador().getIdUsuario().equals(idAutor)) {
            throw new AutorNoAutorizadoException("Solo quien administra el grupo puede publicar avisos");
        }
    }
}
