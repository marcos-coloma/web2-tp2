package com.example.demo.domain;

/**
 * Modelo de dominio de una lista de favoritos: identificador, nombre
 * e identificador del usuario propietario. La lista agrupa favoritos
 * mediante sus ids, sin guardar directamente los objetos completos.
 */
public record Lista(
        Long id,
        String nombre,
        Long usuarioId
) {

    // Constructor temporal para mantener compatibilidad con el código existente.
    public Lista(Long id, String nombre) {
        this(id, nombre, null);
    }
}