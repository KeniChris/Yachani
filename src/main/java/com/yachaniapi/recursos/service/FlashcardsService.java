package com.yachaniapi.recursos.service;

import com.yachaniapi.recursos.dto.request.*;
import com.yachaniapi.recursos.dto.response.*;
import com.yachaniapi.recursos.entity.*;
import com.yachaniapi.recursos.exception.FlashcardsException;
import com.yachaniapi.recursos.mapper.FlashcardsMapper;
import com.yachaniapi.recursos.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class FlashcardsService {

    private final MazoFlashcardsRepository mazos;
    private final TarjetaFlashcardRepository tarjetas;
    private final MazoCompartidoRepository compartidos;
    private final ProgresoTarjetaRepository progresos;
    private final TemaGrupoRepository temas;
    private final AccesoFlashcardsService acceso;
    private final AccesoRecursosService accesoGrupos;
    private final FlashcardsMapper mapper;

    public OperacionFlashcardsResponse<MazoResponse> crear(
            MazoRequest request
    ) {
        var mazo = new MazoFlashcards();

        mazo.setCreador(acceso.usuarioActual());
        mazo.setTitulo(request.titulo().strip());
        mazo.setDescripcion(limpiar(request.descripcion()));

        mazos.saveAndFlush(mazo);

        return new OperacionFlashcardsResponse<>(
                "Mazo creado correctamente",
                resumen(mazo)
        );
    }

    @Transactional(readOnly = true)
    public List<MazoResponse> listarPropios() {
        var usuario = acceso.usuarioActual();

        return mazos
                .findByCreador_IdUsuarioOrderByIdMazoDesc(
                        usuario.getIdUsuario()
                )
                .stream()
                .map(this::resumen)
                .toList();
    }

    @Transactional(readOnly = true)
    public MazoDetalleResponse consultarPropio(
            Long idMazo
    ) {
        return detalle(
                acceso.propio(idMazo, false)
        );
    }

    public OperacionFlashcardsResponse<MazoResponse> editar(
            Long idMazo,
            MazoRequest request
    ) {
        var mazo = acceso.propio(idMazo, true);

        mazo.setTitulo(request.titulo().strip());
        mazo.setDescripcion(limpiar(request.descripcion()));

        return new OperacionFlashcardsResponse<>(
                "Mazo actualizado correctamente",
                resumen(mazo)
        );
    }

    public OperacionFlashcardsResponse<TarjetaResponse>
    agregarTarjeta(
            Long idMazo,
            TarjetaRequest request
    ) {
        var mazo = acceso.propio(idMazo, true);
        var tarjeta = new TarjetaFlashcard();

        tarjeta.setMazo(mazo);
        tarjeta.setPregunta(request.pregunta().strip());
        tarjeta.setRespuesta(request.respuesta().strip());
        tarjeta.setOrden(
                tarjetas.ultimoOrden(idMazo) + 1
        );

        tarjetas.saveAndFlush(tarjeta);

        return new OperacionFlashcardsResponse<>(
                "Tarjeta agregada correctamente",
                mapper.tarjeta(tarjeta)
        );
    }

    public OperacionFlashcardsResponse<TarjetaResponse>
    editarTarjeta(
            Long idMazo,
            Long idTarjeta,
            TarjetaRequest request
    ) {
        acceso.propio(idMazo, true);

        var tarjeta = buscarTarjeta(idMazo, idTarjeta);

        tarjeta.setPregunta(request.pregunta().strip());
        tarjeta.setRespuesta(request.respuesta().strip());

        return new OperacionFlashcardsResponse<>(
                "Tarjeta actualizada correctamente",
                mapper.tarjeta(tarjeta)
        );
    }

    public void eliminarTarjeta(
            Long idMazo,
            Long idTarjeta
    ) {
        var mazo = acceso.propio(idMazo, true);
        var tarjeta = buscarTarjeta(idMazo, idTarjeta);

        if (mazo.getEstado() == EstadoMazo.PUBLICADO
                && tarjetas.countByMazo_IdMazoAndActivaTrue(
                idMazo
        ) == 1) {

            throw new FlashcardsException(
                    HttpStatus.CONFLICT,
                    "Retira primero la publicación para eliminar la última tarjeta"
            );
        }

        tarjeta.setActiva(false);
    }

    public OperacionFlashcardsResponse<MazoCompartidoResponse> publicarEnGrupo(
            Long idGrupo,
            Long idMazo,
            CompartirMazoRequest request
    ) {
        var mazo = acceso.propio(idMazo, true);

        var contexto = accesoGrupos.obtener(
                idGrupo,
                false
        );

        var tema = temas
                .findByIdTemaAndGrupo_IdGrupo(
                        request.idTema(),
                        idGrupo
                )
                .orElseThrow(() -> new FlashcardsException(
                        HttpStatus.NOT_FOUND,
                        "El tema no existe dentro de este grupo"
                ));

        var contenido = tarjetas
                .findByMazo_IdMazoAndActivaTrueOrderByOrdenAscIdTarjetaAsc(
                        idMazo
                );

        if (contenido.isEmpty()
                || contenido.stream().anyMatch(tarjeta ->
                vacio(tarjeta.getPregunta())
                        || vacio(tarjeta.getRespuesta())
        )) {

            throw new FlashcardsException(
                    HttpStatus.BAD_REQUEST,
                    "Complete el contenido de todas las tarjetas y seleccione un tema antes de publicar"
            );
        }

        var compartido = compartidos
                .findByMazo_IdMazoAndGrupo_IdGrupo(
                        idMazo,
                        idGrupo
                )
                .orElseGet(MazoCompartido::new);

        compartido.setMazo(mazo);
        compartido.setGrupo(contexto.grupo());
        compartido.setTema(tema);

        mazo.setEstado(EstadoMazo.PUBLICADO);

        compartidos.saveAndFlush(compartido);

        return new OperacionFlashcardsResponse<>(
                "Flashcards publicadas y compartidas correctamente",
                mapper.compartido(
                        compartido,
                        contenido.size()
                )
        );
    }

    @Transactional(readOnly = true)
    public List<MazoCompartidoResponse> listarGrupo(
            Long idGrupo,
            Long idTema
    ) {
        accesoGrupos.obtener(idGrupo, false);

        if (idTema != null
                && temas.findByIdTemaAndGrupo_IdGrupo(
                idTema,
                idGrupo
        ).isEmpty()) {

            throw new FlashcardsException(
                    HttpStatus.NOT_FOUND,
                    "El tema no existe dentro de este grupo"
            );
        }

        return compartidos
                .findByGrupo_IdGrupoAndMazo_EstadoOrderByIdCompartidoDesc(
                        idGrupo,
                        EstadoMazo.PUBLICADO
                )
                .stream()
                .filter(compartido ->
                        idTema == null
                                || compartido.getTema().getIdTema()
                                .equals(idTema)
                )
                .map(compartido -> mapper.compartido(
                        compartido,
                        tarjetas.countByMazo_IdMazoAndActivaTrue(
                                compartido.getMazo().getIdMazo()
                        )
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public MazoDetalleResponse consultarGrupo(
            Long idGrupo,
            Long idMazo
    ) {
        accesoGrupos.obtener(idGrupo, false);

        var compartido = buscarCompartido(
                idGrupo,
                idMazo
        );

        if (compartido.getMazo().getEstado()
                != EstadoMazo.PUBLICADO) {

            throw new FlashcardsException(
                    HttpStatus.NOT_FOUND,
                    "El mazo no está publicado en este grupo"
            );
        }

        return detalle(compartido.getMazo());
    }

    public void dejarDeCompartir(
            Long idGrupo,
            Long idMazo
    ) {
        var usuario = acceso.usuarioActual();
        var mazo = acceso.buscar(idMazo, true);

        var contexto = accesoGrupos.obtener(
                idGrupo,
                false
        );

        if (!acceso.esAutor(mazo, usuario)
                && !contexto.administrador()) {

            throw new FlashcardsException(
                    HttpStatus.FORBIDDEN,
                    "Solo el autor o el administrador pueden retirar el mazo del grupo"
            );
        }

        compartidos.delete(
                buscarCompartido(idGrupo, idMazo)
        );
    }

    public OperacionFlashcardsResponse<MazoResponse>
    retirarPublicacion(
            Long idMazo
    ) {
        var mazo = acceso.propio(idMazo, true);

        mazo.setEstado(EstadoMazo.BORRADOR);

        return new OperacionFlashcardsResponse<>(
                "Publicación retirada; el mazo y el progreso se conservan",
                resumen(mazo)
        );
    }

    @Transactional(readOnly = true)
    public ProgresoResponse consultarProgreso(
            Long idMazo
    ) {
        var contexto = acceso.paraPracticar(
                idMazo,
                false
        );

        return progreso(
                idMazo,
                contexto.usuario().getIdUsuario()
        );
    }

    public OperacionFlashcardsResponse<ProgresoResponse>
    guardarProgreso(
            Long idMazo,
            Long idTarjeta,
            ProgresoRequest request
    ) {
        var contexto = acceso.paraPracticar(
                idMazo,
                true
        );

        var tarjeta = buscarTarjeta(
                idMazo,
                idTarjeta
        );

        Long idUsuario =
                contexto.usuario().getIdUsuario();

        var progreso = progresos
                .findByUsuario_IdUsuarioAndTarjeta_IdTarjeta(
                        idUsuario,
                        idTarjeta
                )
                .orElseGet(ProgresoTarjeta::new);

        progreso.setUsuario(contexto.usuario());
        progreso.setTarjeta(tarjeta);
        progreso.setVista(true);
        progreso.setAprendida(request.aprendida());
        progreso.setFechaUltimaPractica(
                LocalDateTime.now()
        );

        progresos.saveAndFlush(progreso);

        return new OperacionFlashcardsResponse<>(
                "Progreso guardado correctamente",
                progreso(idMazo, idUsuario)
        );
    }

    private MazoResponse resumen(
            MazoFlashcards mazo
    ) {
        return mapper.mazo(
                mazo,
                tarjetas.countByMazo_IdMazoAndActivaTrue(
                        mazo.getIdMazo()
                )
        );
    }

    private MazoDetalleResponse detalle(
            MazoFlashcards mazo
    ) {
        return mapper.detalle(
                mazo,
                tarjetas
                        .findByMazo_IdMazoAndActivaTrueOrderByOrdenAscIdTarjetaAsc(
                                mazo.getIdMazo()
                        )
        );
    }

    private ProgresoResponse progreso(
            Long idMazo,
            Long idUsuario
    ) {
        return mapper.progreso(
                idMazo,
                tarjetas
                        .findByMazo_IdMazoAndActivaTrueOrderByOrdenAscIdTarjetaAsc(
                                idMazo
                        ),
                progresos
                        .findByUsuario_IdUsuarioAndTarjeta_Mazo_IdMazo(
                                idUsuario,
                                idMazo
                        )
        );
    }

    private TarjetaFlashcard buscarTarjeta(
            Long idMazo,
            Long idTarjeta
    ) {
        return tarjetas
                .findByIdTarjetaAndMazo_IdMazoAndActivaTrue(
                        idTarjeta,
                        idMazo
                )
                .orElseThrow(() -> new FlashcardsException(
                        HttpStatus.NOT_FOUND,
                        "La tarjeta no existe dentro de este mazo"
                ));
    }

    private MazoCompartido buscarCompartido(
            Long idGrupo,
            Long idMazo
    ) {
        return compartidos
                .findByMazo_IdMazoAndGrupo_IdGrupo(
                        idMazo,
                        idGrupo
                )
                .orElseThrow(() -> new FlashcardsException(
                        HttpStatus.NOT_FOUND,
                        "El mazo no está compartido en este grupo"
                ));
    }


    private String limpiar(String texto) {
        return texto == null
                ? null
                : texto.strip();
    }

    private boolean vacio(String texto) {
        return texto == null || texto.isBlank();
    }
}