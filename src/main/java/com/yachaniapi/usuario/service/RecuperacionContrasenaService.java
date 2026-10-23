package com.yachaniapi.usuario.service;

import com.yachaniapi.usuario.dto.RecuperarContrasenaRequest;
import com.yachaniapi.usuario.dto.RestablecerContrasenaRequest;
import com.yachaniapi.usuario.dto.VerificarCodigoRequest;
import com.yachaniapi.usuario.entity.CodigoRecuperacion;
import com.yachaniapi.usuario.entity.Usuario;
import com.yachaniapi.usuario.exception.CodigoRecuperacionExpiradoException;
import com.yachaniapi.usuario.exception.CodigoRecuperacionInvalidoException;
import com.yachaniapi.usuario.repository.CodigoRecuperacionRepository;
import com.yachaniapi.usuario.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Locale;

@Service
public class RecuperacionContrasenaService {

    private static final int MINUTOS_VALIDEZ = 10;

    private final UsuarioRepository usuarioRepository;
    private final CodigoRecuperacionRepository codigoRepository;
    private final PasswordEncoder passwordEncoder;
    private final CorreoService correoService;
    private final SecureRandom secureRandom = new SecureRandom();

    public RecuperacionContrasenaService(
            UsuarioRepository usuarioRepository,
            CodigoRecuperacionRepository codigoRepository,
            PasswordEncoder passwordEncoder,
            CorreoService correoService) {

        this.usuarioRepository = usuarioRepository;
        this.codigoRepository = codigoRepository;
        this.passwordEncoder = passwordEncoder;
        this.correoService = correoService;
    }

    /**
     * Genera y envía un código de recuperación al correo registrado.
     */
    @Transactional
    public void solicitarCodigo(
            RecuperarContrasenaRequest request) {

        String correo = normalizarCorreo(request.getCorreo());

        Usuario usuario = usuarioRepository
                .findByCorreoIgnoreCase(correo)
                .orElse(null);

        /*
         * No se muestra si el correo existe o no, para evitar
         * revelar qué usuarios están registrados.
         */
        if (usuario == null) {
            return;
        }

        // Elimina cualquier código anterior del usuario.
        codigoRepository.deleteByUsuario_IdUsuario(
                usuario.getIdUsuario()
        );

        String codigo = generarCodigo();

        CodigoRecuperacion recuperacion =
                new CodigoRecuperacion();

        recuperacion.setUsuario(usuario);
        recuperacion.setCodigoHash(
                passwordEncoder.encode(codigo)
        );
        recuperacion.setFechaExpiracion(
                LocalDateTime.now().plusMinutes(MINUTOS_VALIDEZ)
        );
        recuperacion.setUtilizado(false);

        codigoRepository.save(recuperacion);

        correoService.enviarCodigoRecuperacion(
                usuario.getCorreo(),
                codigo
        );
    }

    /**
     * Comprueba que el código corresponda al usuario
     * y todavía se encuentre vigente.
     */
    @Transactional
    public void verificarCodigo(
            VerificarCodigoRequest request) {

        Usuario usuario = buscarUsuario(
                request.getCorreo()
        );

        validarCodigo(
                usuario,
                request.getCodigo()
        );
    }

    /**
     * Cambia la contraseña después de comprobar el código.
     */
    @Transactional
    public void restablecerContrasena(
            RestablecerContrasenaRequest request) {

        if (!request.getNuevaContrasena().equals(
                request.getConfirmarContrasena())) {

            throw new IllegalArgumentException(
                    "Las contraseñas no coinciden"
            );
        }

        Usuario usuario = buscarUsuario(
                request.getCorreo()
        );

        CodigoRecuperacion recuperacion =
                validarCodigo(
                        usuario,
                        request.getCodigo()
                );

        usuario.setContrasenaHash(
                passwordEncoder.encode(
                        request.getNuevaContrasena()
                )
        );

        usuarioRepository.save(usuario);

        // El mismo código ya no podrá utilizarse nuevamente.
        recuperacion.setUtilizado(true);
        codigoRepository.save(recuperacion);
    }

    /**
     * Busca al usuario mediante su correo institucional.
     */
    private Usuario buscarUsuario(String correo) {

        return usuarioRepository
                .findByCorreoIgnoreCase(
                        normalizarCorreo(correo)
                )
                .orElseThrow(() ->
                        new CodigoRecuperacionInvalidoException(
                                "El código ingresado no es válido"
                        )
                );
    }

    /**
     * Valida que el código exista, no haya vencido
     * y corresponda al enviado por correo.
     */
    private CodigoRecuperacion validarCodigo(
            Usuario usuario,
            String codigo) {

        CodigoRecuperacion recuperacion =
                codigoRepository
                        .findTopByUsuario_IdUsuarioAndUtilizadoFalseOrderByIdCodigoDesc(
                                usuario.getIdUsuario()
                        )
                        .orElseThrow(() ->
                                new CodigoRecuperacionInvalidoException(
                                        "El código ingresado no es válido"
                                )
                        );

        if (recuperacion.getFechaExpiracion()
                .isBefore(LocalDateTime.now())) {

            throw new CodigoRecuperacionExpiradoException(
                    "El código de recuperación ha vencido"
            );
        }

        boolean codigoCorrecto = passwordEncoder.matches(
                codigo,
                recuperacion.getCodigoHash()
        );

        if (!codigoCorrecto) {
            throw new CodigoRecuperacionInvalidoException(
                    "El código ingresado no es válido"
            );
        }

        return recuperacion;
    }

    /**
     * Genera un código aleatorio de seis números.
     */
    private String generarCodigo() {

        int numero = secureRandom.nextInt(900000) + 100000;

        return String.valueOf(numero);
    }

    /**
     * Quita espacios y normaliza el correo.
     */
    private String normalizarCorreo(String correo) {

        return correo
                .trim()
                .toLowerCase(Locale.ROOT);
    }
}