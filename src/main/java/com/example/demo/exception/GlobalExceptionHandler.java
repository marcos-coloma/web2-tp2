




package com.example.demo.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.LinkedHashMap;
import java.util.Map;


/**
 * Manejador centralizado de errores de toda la API. En vez de que cada
 * controller arme su propio JSON de error, cualquier excepción que llegue
 * hasta acá se traduce a un mismo formato (ProblemDetail, RFC 7807):
 * { "status", "title", "detail", ... }.
 *
 * Cuando se implemente el controller de favoritos en clase, no hace falta
 * tocar esta clase: alcanza con que FavoritoService lance
 * RecursoNoEncontradoException o que el DTO de entrada tenga anotaciones
 * de Bean Validation para que estos mismos handlers respondan.
 */



@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ProblemDetail handleNoEncontrado(RecursoNoEncontradoException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(ExternalServiceException.class)
    public ProblemDetail handleExternalService(ExternalServiceException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_GATEWAY,
                ex.getMessage()
        );
        problem.setTitle("Falla al consumir un servicio externo");
        return problem;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new LinkedHashMap<>();

        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "Uno o más campos no son válidos"
        );
        problem.setTitle("Error de validación");
        problem.setProperty("errores", errors);

        return problem;
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ProblemDetail handleParameterValidation(
            HandlerMethodValidationException ex) {

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "Uno o más parámetros no son válidos"
        );
        problem.setTitle("Error de validación");

        return problem;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGenerico(Exception ex) {
        // Este es el único caso donde SÍ logueamos la excepción completa: es
        // un error no anticipado, y sin esto el detalle real se pierde
        // (el cliente solo debe ver un mensaje genérico, nunca un stack trace).
        log.error("Error inesperado no manejado", ex);
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR, "Ocurrió un error inesperado");
        problema.setTitle("Error interno");
        return problema;
    }
}