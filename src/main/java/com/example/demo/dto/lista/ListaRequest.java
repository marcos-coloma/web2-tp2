package com.example.demo.dto.lista;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO de entrada para crear una lista de favoritos.
 * El cliente solamente proporciona el nombre.
 */
public record ListaRequest(
        @NotBlank(message = "El nombre de la lista es obligatorio")
        String nombre
) {
}