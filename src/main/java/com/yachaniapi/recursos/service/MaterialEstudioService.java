package com.yachaniapi.recursos.service;

import com.yachaniapi.recursos.entity.MaterialEstudio;
import com.yachaniapi.recursos.entity.TemaGrupo;
import com.yachaniapi.recursos.exception.RecursosException;
import com.yachaniapi.recursos.mapper.RecursosMapper;
import com.yachaniapi.recursos.repository.MaterialEstudioRepository;
import com.yachaniapi.recursos.dto.request.CambiarTemaRequest;
import com.yachaniapi.recursos.dto.response.DescargaResponse;
import com.yachaniapi.recursos.dto.response.EnvioResponse;
import com.yachaniapi.recursos.dto.response.MaterialResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
public class MaterialEstudioService {

    private static final Logger log =
            LoggerFactory.getLogger(MaterialEstudioService.class);

    private final MaterialEstudioRepository materiales;
    private final TemaGrupoService temas;
    private final AccesoRecursosService acceso;
    private final ValidadorMaterialService validador;
    private final AlmacenamientoMaterialService almacenamiento;
    private final RecursosMapper mapper;
    private final int maxArchivos;

    public MaterialEstudioService(
            MaterialEstudioRepository materiales,
            TemaGrupoService temas,
            AccesoRecursosService acceso,
            ValidadorMaterialService validador,
            AlmacenamientoMaterialService almacenamiento,
            RecursosMapper mapper,
            @Value("${recursos.max-archivos:5}") int maxArchivos) {

        this.materiales = materiales;
        this.temas = temas;
        this.acceso = acceso;
        this.validador = validador;
        this.almacenamiento = almacenamiento;
        this.mapper = mapper;
        this.maxArchivos = maxArchivos;
    }

    @Transactional
    public EnvioResponse subir(
            Long idGrupo,
            Long idTema,
            List<MultipartFile> archivos) {

        var contexto = acceso.obtener(idGrupo, true);

        if (archivos == null || archivos.isEmpty()) {
            throw new RecursosException(
                    HttpStatus.BAD_REQUEST,
                    "Selecciona al menos un archivo"
            );
        }

        if (archivos.size() > maxArchivos) {
            throw new RecursosException(
                    HttpStatus.BAD_REQUEST,
                    "Se llegó al límite de archivos: máximo "
                            + maxArchivos
                            + " por envío"
            );
        }

        TemaGrupo tema = idTema == null
                ? null
                : temas.buscarEntidad(idGrupo, idTema);

        var validados = archivos.stream()
                .map(validador::validar)
                .toList();

        List<String> idsNube = new ArrayList<>();

        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {

                    @Override
                    public void afterCompletion(int estado) {
                        if (estado == STATUS_ROLLED_BACK) {
                            for (String id : idsNube) {
                                limpiarNube(
                                        id,
                                        "raw",
                                        "authenticated"
                                );
                            }
                        }
                    }
                }
        );

        List<MaterialEstudio> nuevos = new ArrayList<>();

        for (var archivo : validados) {

            String id = almacenamiento.nuevoId(
                    idGrupo,
                    archivo.extension()
            );

            idsNube.add(id);

            almacenamiento.subir(id, archivo);

            MaterialEstudio material = new MaterialEstudio();

            material.setGrupo(contexto.grupo());
            material.setTema(tema);
            material.setAutor(contexto.usuario());
            material.setNombreArchivo(archivo.nombre());
            material.setTipoArchivo(archivo.tipo());
            material.setTamanoArchivo(
                    (long) archivo.contenido().length
            );
            material.setIdArchivoNube(id);
            material.setTipoRecursoNube("raw");
            material.setTipoAccesoNube("authenticated");

            nuevos.add(material);
        }

        var respuestas = materiales.saveAllAndFlush(nuevos)
                .stream()
                .map(mapper::material)
                .toList();

        return new EnvioResponse(
                "Se envió correctamente",
                respuestas
        );
    }

    @Transactional(readOnly = true)
    public Page<MaterialResponse> listar(
            Long idGrupo,
            Long idTema,
            int page,
            int size) {

        acceso.obtener(idGrupo, false);

        var pageable = PageRequest.of(
                page,
                size,
                Sort.by(
                        Sort.Order.desc("fechaPublicacion"),
                        Sort.Order.desc("idMaterial")
                )
        );

        Page<MaterialEstudio> resultado;

        if (idTema == null) {

            resultado = materiales.findByGrupo_IdGrupo(
                    idGrupo,
                    pageable
            );

        } else {

            temas.buscarEntidad(idGrupo, idTema);

            resultado = materiales.findByGrupo_IdGrupoAndTema_IdTema(
                    idGrupo,
                    idTema,
                    pageable
            );
        }

        return resultado.map(mapper::material);
    }

    @Transactional(readOnly = true)
    public MaterialResponse consultar(
            Long idGrupo,
            Long idMaterial) {

        acceso.obtener(idGrupo, false);

        return mapper.material(
                buscar(idGrupo, idMaterial)
        );
    }

    @Transactional
    public MaterialResponse cambiarTema(
            Long idGrupo,
            Long idMaterial,
            CambiarTemaRequest request) {

        acceso.obtener(idGrupo, true);

        MaterialEstudio material = buscar(
                idGrupo,
                idMaterial
        );

        material.setTema(
                request.idTema() == null
                        ? null
                        : temas.buscarEntidad(
                        idGrupo,
                        request.idTema()
                )
        );

        return mapper.material(
                materiales.saveAndFlush(material)
        );
    }

    @Transactional(readOnly = true)
    public DescargaResponse descargar(
            Long idGrupo,
            Long idMaterial) {

        acceso.obtener(idGrupo, false);

        return almacenamiento.descarga(
                buscar(idGrupo, idMaterial)
        );
    }

    @Transactional
    public void eliminar(
            Long idGrupo,
            Long idMaterial) {

        var contexto = acceso.obtener(idGrupo, true);

        MaterialEstudio material = buscar(
                idGrupo,
                idMaterial
        );

        boolean esAutor = material.getAutor()
                .getIdUsuario()
                .equals(contexto.usuario().getIdUsuario());

        if (!contexto.administrador() && !esAutor) {
            throw new RecursosException(
                    HttpStatus.FORBIDDEN,
                    "Solo el autor o el administrador pueden eliminar este material"
            );
        }

        String idNube = material.getIdArchivoNube();
        String tipoRecurso = material.getTipoRecursoNube();
        String tipoAcceso = material.getTipoAccesoNube();

        materiales.delete(material);
        materiales.flush();

        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {

                    @Override
                    public void afterCommit() {
                        limpiarNube(
                                idNube,
                                tipoRecurso,
                                tipoAcceso
                        );
                    }
                }
        );
    }

    private MaterialEstudio buscar(
            Long idGrupo,
            Long idMaterial) {

        return materiales.findByIdMaterialAndGrupo_IdGrupo(
                        idMaterial,
                        idGrupo
                )
                .orElseThrow(() ->
                        new RecursosException(
                                HttpStatus.NOT_FOUND,
                                "El material no existe dentro de este grupo"
                        )
                );
    }

    private void limpiarNube(
            String id,
            String tipoRecurso,
            String tipoAcceso) {

        try {
            almacenamiento.eliminar(
                    id,
                    tipoRecurso,
                    tipoAcceso
            );

        } catch (Exception exception) {
            log.error(
                    "Archivo pendiente de eliminar en la nube: {}",
                    id,
                    exception
            );
        }
    }
}