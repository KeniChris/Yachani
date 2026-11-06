package com.yachaniapi.usuario.service;

import com.yachaniapi.usuario.dto.*;
import com.yachaniapi.usuario.entity.*;
import com.yachaniapi.usuario.exception.CorreoDuplicadoException;
import com.yachaniapi.usuario.exception.CredencialesIncorrectasException;
import com.yachaniapi.usuario.mapper.UsuarioMapper;
import com.yachaniapi.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioMapper usuarioMapper;
    private final JwtService jwtService;
    private final UsuarioActualService usuarioActual;

    @Transactional
    public UsuarioResponse registrar(RegistroRequest request) {
        validarContrasena(request.getContrasena());
        String correo = request.getCorreo().strip().toLowerCase(Locale.ROOT);
        if (usuarioRepository.existsByCorreoIgnoreCase(correo)) {
            throw new CorreoDuplicadoException("El correo ya está registrado");
        }
        Usuario usuario = switch (request.getTipoUsuario().strip().toUpperCase(Locale.ROOT)) {
            case "ESTUDIANTE" -> new Estudiante();
            case "TUTOR" -> new Tutor();
            default -> throw new IllegalArgumentException("El tipo de usuario debe ser ESTUDIANTE o TUTOR");
        };
        usuario.setNombres(request.getNombres().strip());
        usuario.setApellidos(request.getApellidos().strip());
        usuario.setCorreo(correo);
        usuario.setContrasenaHash(passwordEncoder.encode(request.getContrasena()));
        return usuarioMapper.toResponse(usuarioRepository.save(usuario), "Usuario registrado correctamente");
    }

    public LoginResponse iniciarSesion(LoginRequest request) {
        String correo = request.getCorreo().strip().toLowerCase(Locale.ROOT);
        Usuario usuario = usuarioRepository.findByCorreoIgnoreCase(correo)
                .filter(u -> passwordEncoder.matches(request.getContrasena(), u.getContrasenaHash()))
                .orElseThrow(() -> new CredencialesIncorrectasException("Correo o contraseña incorrectos"));
        return new LoginResponse(usuarioMapper.toResponse(usuario, "Inicio de sesión correcto"),
                jwtService.generar(usuario), jwtService.segundosDeValidez());
    }

    public UsuarioResponse consultarActual() {
        return usuarioMapper.toResponse(usuarioActual.obtener(), "Usuario autenticado");
    }

    private void validarContrasena(String contrasena) {
        if (contrasena == null || contrasena.length() < 8
                || contrasena.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new IllegalArgumentException("La contraseña requiere al menos 8 caracteres y hasta 72 bytes");
        }
    }
}
