package com.example.demo.exception;

/**
 * Excepción genérica de "no encontrado" (404). La usa hoy el módulo de
 * productos cuando DummyJSON no tiene el id pedido; el mismo tipo sirve
 * para favoritos cuando se implemente su service — no hace falta crear una
 * excepción nueva por cada recurso.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}