package com.example.demo.domain;

/**
 * Modelo de dominio de una lista de favoritos: un identificador y un nombre.
 * La lista agrupa favoritos mediante sus ids, sin guardar directamente los
 * datos completos de los favoritos.
 */

public record Lista(
        Long id,
        String nombre
) {
}