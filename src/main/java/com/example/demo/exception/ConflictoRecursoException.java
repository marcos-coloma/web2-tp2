package com.example.demo.exception;

/**
 * Excepción que representa un conflicto con el estado actual de un recurso.
 * Se utiliza cuando una operación no puede realizarse porque existe una
 * relación o condición que lo impide.
 */
public class ConflictoRecursoException extends RuntimeException {

    public ConflictoRecursoException(String mensaje) {
        super(mensaje);
    }
}