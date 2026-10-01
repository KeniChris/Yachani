package com.yachaniapi.recursos.exception;

import com.yachaniapi.recursos.dto.RecursosDTO.ErrorResponse;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.time.LocalDateTime;
import java.util.List;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice(
        basePackages = "com.yachaniapi.recursos.controller"
)
public class RecursosExceptionHandler {

    private static final Logger log =
            LoggerFactory.getLogger(RecursosExceptionHandler.class);

    @ExceptionHandler(RecursosException.class)
    public ResponseEntity<ErrorResponse> manejarRecurso(
            RecursosException exception) {

        return respuesta(
                exception.getEstado(),
                exception.getMessage(),
                List.of()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> manejarDTO(
            MethodArgumentNotValidException exception) {

        List<String> errores = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error ->
                        error.getField() + ": " + error.getDefaultMessage()
                )
                .toList();

        return respuesta(
                HttpStatus.BAD_REQUEST,
                "Revisa los datos enviados",
                errores
        );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> manejarParametros(
            ConstraintViolationException exception) {

        List<String> errores = exception.getConstraintViolations()
                .stream()
                .map(error -> error.getMessage())
                .sorted()
                .toList();

        return respuesta(
                HttpStatus.BAD_REQUEST,
                "Revisa los parámetros",
                errores
        );
    }

    @ExceptionHandler({
            HttpMessageNotReadableException.class,
            MethodArgumentTypeMismatchException.class,
            MissingServletRequestParameterException.class,
            MissingServletRequestPartException.class
    })
    public ResponseEntity<ErrorResponse> manejarPeticion(
            Exception exception) {

        return respuesta(
                HttpStatus.BAD_REQUEST,
                "La petición está incompleta o contiene datos inválidos",
                List.of()
        );
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> manejarTamano(
            MaxUploadSizeExceededException exception) {

        return respuesta(
                HttpStatus.PAYLOAD_TOO_LARGE,
                "El envío supera el tamaño máximo permitido",
                List.of()
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> manejarRestriccion(
            DataIntegrityViolationException exception) {

        log.error("Restricción de datos en recursos", exception);

        return respuesta(
                HttpStatus.CONFLICT,
                "Los cambios no pudieron guardarse por una restricción de datos",
                List.of()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> manejarInesperado(
            Exception exception) {

        log.error("Error inesperado en recursos", exception);

        return respuesta(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "No se pudo completar la operación",
                List.of()
        );
    }

    private ResponseEntity<ErrorResponse> respuesta(
            HttpStatus estado,
            String mensaje,
            List<String> errores) {

        return ResponseEntity.status(estado)
                .body(new ErrorResponse(
                        LocalDateTime.now(),
                        estado.value(),
                        mensaje,
                        errores
                ));
    }
}