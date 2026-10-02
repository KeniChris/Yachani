package com.yachaniapi.usuario.service;

import com.yachaniapi.usuario.dto.LoginRequest;
import com.yachaniapi.usuario.dto.RegistroRequest;
import com.yachaniapi.usuario.dto.UsuarioResponse;
import com.yachaniapi.usuario.entity.Estudiante;
import com.yachaniapi.usuario.entity.Tutor;
import com.yachaniapi.usuario.entity.Usuario;
import com.yachaniapi.usuario.exception.CorreoDuplicadoException;
import com.yachaniapi.usuario.exception.CredencialesIncorrectasException;
import com.yachaniapi.usuario.mapper.UsuarioMapper;
import com.yachaniapi.usuario.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioMapper usuarioMapper;

    public AuthService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            UsuarioMapper usuarioMapper) {

        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.usuarioMapper = usuarioMapper;
    }

    /**
     * Registra un estudiante o tutor en la plataforma.
     */
    public UsuarioResponse registrar(RegistroRequest request) {

        validarRegistro(request);

        String correo = request.getCorreo()
                .trim()
                .toLowerCase();

        // Evita registrar dos cuentas con el mismo correo.
        if (usuarioRepository.existsByCorreoIgnoreCase(correo)) {
            throw new CorreoDuplicadoException(
                    "El correo ya está registrado"
            );
        }

        Usuario usuario;

        // Crea la entidad según el tipo de usuario seleccionado.
        if ("ESTUDIANTE".equalsIgnoreCase(request.getTipoUsuario())) {
            usuario = new Estudiante();

        } else if ("TUTOR".equalsIgnoreCase(request.getTipoUsuario())) {
            usuario = new Tutor();

        } else {
            throw new IllegalArgumentException(
                    "El tipo de usuario debe ser ESTUDIANTE o TUTOR"
            );
        }

        usuario.setNombres(request.getNombres().trim());
        usuario.setApellidos(request.getApellidos().trim());
        usuario.setCorreo(correo);

        // La contraseña se guarda cifrada mediante BCrypt.
        usuario.setContrasenaHash(
                passwordEncoder.encode(request.getContrasena())
        );

        Usuario usuarioGuardado = usuarioRepository.save(usuario);

        return usuarioMapper.toResponse(
                usuarioGuardado,
                "Usuario registrado correctamente"
        );
    }

    /**
     * Comprueba el correo y la contraseña del usuario.
     */
    public UsuarioResponse iniciarSesion(LoginRequest request) {

        if (request == null
                || estaVacio(request.getCorreo())
                || estaVacio(request.getContrasena())) {

            throw new IllegalArgumentException(
                    "El correo y la contraseña son obligatorios"
            );
        }

        String correo = request.getCorreo()
                .trim()
                .toLowerCase();

        Usuario usuario = usuarioRepository
                .findByCorreoIgnoreCase(correo)
                .orElseThrow(() ->
                        new CredencialesIncorrectasException(
                                "Correo o contraseña incorrectos"
                        )
                );

        boolean contrasenaCorrecta = passwordEncoder.matches(
                request.getContrasena(),
                usuario.getContrasenaHash()
        );

        if (!contrasenaCorrecta) {
            throw new CredencialesIncorrectasException(
                    "Correo o contraseña incorrectos"
            );
        }

        return usuarioMapper.toResponse(
                usuario,
                "Inicio de sesión correcto"
        );
    }

    /**
     * Valida los campos necesarios para registrar una cuenta.
     */
    private void validarRegistro(RegistroRequest request) {

        if (request == null
                || estaVacio(request.getNombres())
                || estaVacio(request.getApellidos())
                || estaVacio(request.getCorreo())
                || estaVacio(request.getContrasena())
                || estaVacio(request.getTipoUsuario())) {

            throw new IllegalArgumentException(
                    "Todos los campos son obligatorios"
            );
        }

        String correo = request.getCorreo()
                .trim()
                .toLowerCase();

        // Solo permite correos institucionales de la UPC.
        if (!correo.matches("^[A-Za-z0-9._%+-]+@upc\\.edu\\.pe$")) {
            throw new IllegalArgumentException(
                    "El correo debe pertenecer al dominio @upc.edu.pe"
            );
        }
    }

    /**
     * Comprueba si un texto es nulo o está vacío.
     */
    private boolean estaVacio(String valor) {
        return valor == null || valor.isBlank();
    }
}