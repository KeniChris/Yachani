package com.yachaniapi.exception;

import com.yachaniapi.usuario.exception.CorreoDuplicadoException;
import com.yachaniapi.usuario.exception.CredencialesIncorrectasException;
import com.yachaniapi.usuario.exception.UsuarioNoEncontradoException;
import com.yachaniapi.usuario.exception.UsuarioNoEsEstudianteException;
import com.yachaniapi.grupoestudio.exception.GrupoEstudioExceptions.*;
import com.yachaniapi.resena.exception.ResenaExceptions.*;
import com.yachaniapi.sesion.exception.SesionExceptions.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import com.yachaniapi.chat.exception.ChatExceptions.*;
import com.yachaniapi.notificacion.exception.NotificacionExceptions.NotificacionNoEncontradaException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.yachaniapi.usuario.exception.ArchivoResumenInvalidoException;
import com.yachaniapi.usuario.exception.ResumenNoEncontradoException;
import com.yachaniapi.usuario.exception.UsuarioNoEsTutorException;
import com.yachaniapi.usuario.exception.CodigoRecuperacionInvalidoException;
import com.yachaniapi.usuario.exception.CodigoRecuperacionExpiradoException;
import com.yachaniapi.usuario.exception.EnvioCorreoException;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UsuarioNoEncontradoException.class)
    public ResponseEntity<Map<String, String>> manejarUsuarioNoEncontrado(
            UsuarioNoEncontradoException exception) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("mensaje", exception.getMessage()));
    }

    @ExceptionHandler(CorreoDuplicadoException.class)
    public ResponseEntity<Map<String, String>> manejarCorreoDuplicado(
            CorreoDuplicadoException exception) {

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("mensaje", exception.getMessage()));
    }

    @ExceptionHandler(CredencialesIncorrectasException.class)
    public ResponseEntity<Map<String, String>> manejarCredencialesIncorrectas(
            CredencialesIncorrectasException exception) {

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("mensaje", exception.getMessage()));
    }

    @ExceptionHandler(UsuarioNoEsEstudianteException.class)
    public ResponseEntity<Map<String, String>> manejarUsuarioNoEsEstudiante(
            UsuarioNoEsEstudianteException exception) {

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("mensaje", exception.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> manejarValidacion(
            MethodArgumentNotValidException exception) {

        String mensaje = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(error -> error.getDefaultMessage())
                .orElse("Los datos enviados no son válidos");

        return ResponseEntity.badRequest()
                .body(Map.of("mensaje", mensaje));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> manejarArgumentoInvalido(
            IllegalArgumentException exception) {

        return ResponseEntity.badRequest()
                .body(Map.of("mensaje", exception.getMessage()));
    }

        @ExceptionHandler(GrupoLlenoException.class)
    public ResponseEntity<Map<String, String>> manejarGrupoLleno(
            GrupoLlenoException exception) {

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("mensaje", exception.getMessage()));
    }

    @ExceptionHandler(GrupoNoEncontradoException.class)
    public ResponseEntity<Map<String, String>> manejarGrupoNoEncontrado(
            GrupoNoEncontradoException exception) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("mensaje", exception.getMessage()));
    }

    @ExceptionHandler(SolicitudNoEncontradaException.class)
    public ResponseEntity<Map<String, String>> manejarSolicitudNoEncontrada(
            SolicitudNoEncontradaException exception) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("mensaje", exception.getMessage()));
    }

    @ExceptionHandler(ResumenNoEncontradoException.class)
    public ResponseEntity<Map<String, String>> manejarResumenNoEncontrado(
            ResumenNoEncontradoException exception) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("mensaje", exception.getMessage()));
    }

    @ExceptionHandler(ArchivoResumenInvalidoException.class)
    public ResponseEntity<Map<String, String>> manejarArchivoInvalido(
            ArchivoResumenInvalidoException exception) {

        return ResponseEntity.badRequest()
                .body(Map.of("mensaje", exception.getMessage()));
    }
    @ExceptionHandler(UsuarioNoEsTutorException.class)
    public ResponseEntity<Map<String, String>> manejarUsuarioNoEsTutor(
            UsuarioNoEsTutorException exception) {

        return ResponseEntity.badRequest()
                .body(Map.of("mensaje", exception.getMessage()));
    }

    @ExceptionHandler(ResenaDuplicadaException.class)
    public ResponseEntity<Map<String, String>> manejarResenaDuplicada(
            ResenaDuplicadaException exception) {

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("mensaje", exception.getMessage()));
    }

    @ExceptionHandler(ResenaNoPermitidaException.class)
    public ResponseEntity<Map<String, String>> manejarResenaNoPermitida(
            ResenaNoPermitidaException exception) {

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("mensaje", exception.getMessage()));
    }
    
    @ExceptionHandler(SesionNoEncontradaException.class)
    public ResponseEntity<Map<String, String>> manejarSesionNoEncontrada(
            SesionNoEncontradaException exception) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("mensaje", exception.getMessage()));
    }

    @ExceptionHandler(TutorNoAutorizadoException.class)
    public ResponseEntity<Map<String, String>> manejarTutorNoAutorizado(
            TutorNoAutorizadoException exception) {

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("mensaje", exception.getMessage()));
    }

    @ExceptionHandler(SesionCanceladaException.class)
    public ResponseEntity<Map<String, String>> manejarSesionCancelada(
            SesionCanceladaException exception) {

        return ResponseEntity.badRequest()
                .body(Map.of("mensaje", exception.getMessage()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> manejarFormatoInvalido(
            HttpMessageNotReadableException exception) {

        return ResponseEntity.badRequest()
                .body(Map.of(
                        "mensaje",
                        "Datos inválidos: revisa el formato de los campos"
                ));
    }
    @ExceptionHandler(UsuarioNoPerteneceAlGrupoException.class)
    public ResponseEntity<Map<String, String>> manejarUsuarioNoPerteneceAlGrupo(
            UsuarioNoPerteneceAlGrupoException exception) {

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("mensaje", exception.getMessage()));
    }

    @ExceptionHandler(MensajeNoEncontradoException.class)
    public ResponseEntity<Map<String, String>> manejarMensajeNoEncontrado(
            MensajeNoEncontradoException exception) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("mensaje", exception.getMessage()));
    }

    @ExceptionHandler(MensajeAjenoException.class)
    public ResponseEntity<Map<String, String>> manejarMensajeAjeno(
            MensajeAjenoException exception) {

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("mensaje", exception.getMessage()));
    }

    @ExceptionHandler(TiempoEliminacionExpiradoException.class)
    public ResponseEntity<Map<String, String>> manejarTiempoEliminacionExpirado(
            TiempoEliminacionExpiradoException exception) {

        return ResponseEntity.badRequest()
                .body(Map.of("mensaje", exception.getMessage()));
    }

    @ExceptionHandler(NotificacionNoEncontradaException.class)
    public ResponseEntity<Map<String, String>> manejarNotificacionNoEncontrada(
            NotificacionNoEncontradaException exception) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("mensaje", exception.getMessage()));
    }

    /**
     * Responde cuando el código ingresado no es correcto.
     */
    @ExceptionHandler(CodigoRecuperacionInvalidoException.class)
    public ResponseEntity<Map<String, String>> manejarCodigoInvalido(
            CodigoRecuperacionInvalidoException exception) {

        return ResponseEntity
                .badRequest()
                .body(Map.of(
                        "mensaje",
                        exception.getMessage()
                ));
    }

    /**
     * Responde cuando el código superó su tiempo de validez.
     */
    @ExceptionHandler(CodigoRecuperacionExpiradoException.class)
    public ResponseEntity<Map<String, String>> manejarCodigoExpirado(
            CodigoRecuperacionExpiradoException exception) {

        return ResponseEntity
                .badRequest()
                .body(Map.of(
                        "mensaje",
                        exception.getMessage()
                ));
    }

    /**
     * Responde cuando no fue posible enviar el correo.
     */
    @ExceptionHandler(EnvioCorreoException.class)
    public ResponseEntity<Map<String, String>> manejarErrorEnvioCorreo(
            EnvioCorreoException exception) {

        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                        "mensaje",
                        exception.getMessage()
                ));
    }
}