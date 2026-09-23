package com.yachaniapi.usuario.service;

import com.yachaniapi.usuario.dto.LoginRequest;
import com.yachaniapi.usuario.dto.RegistroRequest;
import com.yachaniapi.usuario.dto.UsuarioResponse;
import com.yachaniapi.usuario.model.Estudiante;
import com.yachaniapi.usuario.model.Tutor;
import com.yachaniapi.usuario.model.Usuario;
import com.yachaniapi.usuario.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UsuarioRepository usuarioRepository,
                       PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Registra un estudiante o tutor en la plataforma.
     */
    public UsuarioResponse registrar(RegistroRequest request) {

        validarRegistro(request);

        String correo = request.getCorreo().trim().toLowerCase();

        // Evita registrar dos cuentas con el mismo correo.
        if (usuarioRepository.existsByCorreoIgnoreCase(correo)) {
            throw new IllegalArgumentException(
                    "El correo ya se encuentra registrado"
            );
        }

        Usuario usuario;

        // Crea la clase correspondiente al tipo seleccionado.
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

        // La contraseña nunca se guarda directamente.
        usuario.setContrasenaHash(
                passwordEncoder.encode(request.getContrasena())
        );

        Usuario usuarioGuardado = usuarioRepository.save(usuario);

        return crearRespuesta(
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

        Usuario usuario = usuarioRepository
                .findByCorreoIgnoreCase(request.getCorreo().trim())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Correo o contraseña incorrectos"
                ));

        boolean contrasenaCorrecta = passwordEncoder.matches(
                request.getContrasena(),
                usuario.getContrasenaHash()
        );

        if (!contrasenaCorrecta) {
            throw new IllegalArgumentException(
                    "Correo o contraseña incorrectos"
            );
        }

        return crearRespuesta(
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
        String correo = request.getCorreo().trim().toLowerCase();

        // El usuario debe registrarse con su correo institucional de la UPC.
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

    /**
     * Convierte la entidad en la respuesta que recibirá el frontend.
     */
    private UsuarioResponse crearRespuesta(
            Usuario usuario,
            String mensaje) {

        String tipoUsuario = usuario instanceof Estudiante
                ? "ESTUDIANTE"
                : "TUTOR";

        return new UsuarioResponse(
                usuario.getIdUsuario(),
                usuario.getNombres(),
                usuario.getApellidos(),
                usuario.getCorreo(),
                tipoUsuario,
                mensaje
        );
    }
}