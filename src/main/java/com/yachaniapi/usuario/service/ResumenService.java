package com.yachaniapi.usuario.service;

import com.yachaniapi.usuario.dto.ArchivoResumenResponse;
import com.yachaniapi.usuario.dto.ResumenResponse;
import com.yachaniapi.usuario.entity.Estudiante;
import com.yachaniapi.usuario.entity.Resumen;
import com.yachaniapi.usuario.entity.Usuario;
import com.yachaniapi.usuario.exception.ArchivoResumenInvalidoException;
import com.yachaniapi.usuario.exception.ResumenNoEncontradoException;
import com.yachaniapi.usuario.exception.UsuarioNoEncontradoException;
import com.yachaniapi.usuario.exception.UsuarioNoEsEstudianteException;
import com.yachaniapi.usuario.mapper.ResumenMapper;
import com.yachaniapi.usuario.repository.ResumenRepository;
import com.yachaniapi.usuario.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class ResumenService {

    // tamaño max de 5 MB.
    private static final long TAMANO_MAXIMO = 1L * 1024 * 1024;

    // archivos permitidos
    private static final Set<String> EXTENSIONES_PERMITIDAS =
            Set.of("pdf", "png", "jpg", "jpeg");

    // Carpeta del proyecto donde se guardarán los archivos
    private final Path directorioArchivos = Paths.get(
            "uploads",
            "resumenes"
    ).toAbsolutePath().normalize();

    private final ResumenRepository resumenRepository;
    private final UsuarioRepository usuarioRepository;
    private final ResumenMapper resumenMapper;

    public ResumenService(
            ResumenRepository resumenRepository,
            UsuarioRepository usuarioRepository,
            ResumenMapper resumenMapper) {

        this.resumenRepository = resumenRepository;
        this.usuarioRepository = usuarioRepository;
        this.resumenMapper = resumenMapper;
    }

    /**
     * Publica un nuevo resumen en el perfil del estudiante
     */
    @Transactional
    public ResumenResponse publicarResumen(
            Long idUsuario,
            String titulo,
            MultipartFile archivo) {

        Estudiante estudiante = buscarEstudiante(idUsuario);

        validarTitulo(titulo);
        validarArchivo(archivo);

        String nombreOriginal = limpiarNombreArchivo(
                archivo.getOriginalFilename()
        );

        Path rutaArchivo = guardarArchivo(archivo, nombreOriginal);

        Resumen resumen = new Resumen();
        resumen.setTitulo(titulo.trim());
        resumen.setNombreArchivo(nombreOriginal);
        resumen.setTipoArchivo(obtenerTipoArchivo(archivo));
        resumen.setRutaArchivo(rutaArchivo.toString());
        resumen.setTamanoArchivo(archivo.getSize());
        resumen.setEstudiante(estudiante);

        Resumen resumenGuardado = resumenRepository.save(resumen);

        return resumenMapper.toResponse(resumenGuardado);
    }

    /**
     * Reemplaza el archivo y actualiza el título de un resumen q ya existe
     */
    @Transactional
    public ResumenResponse reemplazarResumen(
            Long idUsuario,
            Long idResumen,
            String titulo,
            MultipartFile archivo) {

        buscarEstudiante(idUsuario);

        Resumen resumen = buscarResumen(idUsuario, idResumen);

        validarTitulo(titulo);
        validarArchivo(archivo);

        String nombreOriginal = limpiarNombreArchivo(
                archivo.getOriginalFilename()
        );

        Path nuevaRuta = guardarArchivo(archivo, nombreOriginal);

        eliminarArchivoAnterior(resumen.getRutaArchivo());

        resumen.setTitulo(titulo.trim());
        resumen.setNombreArchivo(nombreOriginal);
        resumen.setTipoArchivo(obtenerTipoArchivo(archivo));
        resumen.setRutaArchivo(nuevaRuta.toString());
        resumen.setTamanoArchivo(archivo.getSize());

        Resumen resumenActualizado = resumenRepository.save(resumen);

        return resumenMapper.toResponse(resumenActualizado);
    }

    /**
     * Muestra todos los resúmenes publicados por un estudiante
     */
    public List<ResumenResponse> listarResumenes(Long idUsuario) {

        buscarEstudiante(idUsuario);

        return resumenRepository
                .findByEstudiante_IdUsuarioOrderByFechaPublicacionDesc(
                        idUsuario
                )
                .stream()
                .map(resumenMapper::toResponse)
                .toList();
    }

    /**
     * Busca un resumen por estudiante
     */
    public Resumen buscarResumen(
            Long idUsuario,
            Long idResumen) {

        return resumenRepository
                .findByIdResumenAndEstudiante_IdUsuario(
                        idResumen,
                        idUsuario
                )
                .orElseThrow(() ->
                        new ResumenNoEncontradoException(
                                "El resumen no existe o no pertenece al estudiante"
                        )
                );
    }

    /**
     * Obtiene el archivo de un resumen para ver o descargar
     */
    public ArchivoResumenResponse descargarResumen(
            Long idUsuario,
            Long idResumen) {

        Resumen resumen = buscarResumen(idUsuario, idResumen);

        try {
            Path ruta = Paths.get(resumen.getRutaArchivo())
                    .toAbsolutePath()
                    .normalize();

            Resource archivo = new UrlResource(ruta.toUri());

            if (!archivo.exists() || !archivo.isReadable()) {
                throw new ResumenNoEncontradoException(
                        "El archivo del resumen no está disponible"
                );
            }

            return new ArchivoResumenResponse(
                    archivo,
                    resumen.getNombreArchivo(),
                    resumen.getTipoArchivo()
            );

        } catch (MalformedURLException exception) {
            throw new ResumenNoEncontradoException(
                    "No se pudo acceder al archivo del resumen"
            );
        }
    }

    /**
     * Se comprueba que el usuario sea estudiante
     */
    private Estudiante buscarEstudiante(Long idUsuario) {

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
     * Comprueba que el título sea obligatorio y no sea muy largo
     */
    private void validarTitulo(String titulo) {

        if (titulo == null || titulo.isBlank()) {
            throw new IllegalArgumentException(
                    "El título del resumen es obligatorio"
            );
        }

        if (titulo.trim().length() > 150) {
            throw new IllegalArgumentException(
                    "El título no puede superar los 150 caracteres"
            );
        }
    }

    /**
     *validacion de todas las restricciones para un archivo
     */
    private void validarArchivo(MultipartFile archivo) {

        if (archivo == null || archivo.isEmpty()) {
            throw new ArchivoResumenInvalidoException(
                    "Debe seleccionar un archivo"
            );
        }

        if (archivo.getSize() > TAMANO_MAXIMO) {
            throw new ArchivoResumenInvalidoException(
                    "El archivo no puede superar los 5 MB"
            );
        }

        String nombreArchivo = archivo.getOriginalFilename();

        if (nombreArchivo == null || nombreArchivo.isBlank()) {
            throw new ArchivoResumenInvalidoException(
                    "El archivo debe tener un nombre válido"
            );
        }

        String extension = obtenerExtension(nombreArchivo);

        if (!EXTENSIONES_PERMITIDAS.contains(extension)) {
            throw new ArchivoResumenInvalidoException(
                    "Solo se permiten archivos PDF, PNG, JPG o JPEG"
            );
        }
    }

    /**
     * Guarda el archivo con un nombre único
     */
    private Path guardarArchivo(
            MultipartFile archivo,
            String nombreOriginal) {

        try {
            Files.createDirectories(directorioArchivos);

            String extension = obtenerExtension(nombreOriginal);
            String nombreGuardado =
                    UUID.randomUUID() + "." + extension;

            Path rutaDestino = directorioArchivos
                    .resolve(nombreGuardado)
                    .normalize();

            Files.copy(
                    archivo.getInputStream(),
                    rutaDestino,
                    StandardCopyOption.REPLACE_EXISTING
            );

            return rutaDestino;

        } catch (IOException exception) {
            throw new ArchivoResumenInvalidoException(
                    "No se pudo guardar el archivo"
            );
        }
    }

    /**
     * Elimina el archivo anterior cuando un resumen es reemplazado
     */
    private void eliminarArchivoAnterior(String rutaArchivo) {

        if (rutaArchivo == null || rutaArchivo.isBlank()) {
            return;
        }

        try {
            Files.deleteIfExists(
                    Paths.get(rutaArchivo)
                            .toAbsolutePath()
                            .normalize()
            );
        } catch (IOException exception) {
            throw new ArchivoResumenInvalidoException(
                    "No se pudo reemplazar el archivo anterior"
            );
        }
    }

    /**
     * Evita guardar rutas completas enviadas como nombre del archivo
     */
    private String limpiarNombreArchivo(String nombreArchivo) {

        if (nombreArchivo == null) {
            throw new ArchivoResumenInvalidoException(
                    "El archivo debe tener un nombre válido"
            );
        }

        return Paths.get(nombreArchivo)
                .getFileName()
                .toString();
    }

    private String obtenerExtension(String nombreArchivo) {

        int posicionPunto = nombreArchivo.lastIndexOf('.');

        if (posicionPunto < 0
                || posicionPunto == nombreArchivo.length() - 1) {
            throw new ArchivoResumenInvalidoException(
                    "El archivo debe tener una extensión válida"
            );
        }

        return nombreArchivo
                .substring(posicionPunto + 1)
                .toLowerCase();
    }

    private String obtenerTipoArchivo(MultipartFile archivo) {

        if (archivo.getContentType() == null
                || archivo.getContentType().isBlank()) {
            return MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }

        return archivo.getContentType();
    }
}