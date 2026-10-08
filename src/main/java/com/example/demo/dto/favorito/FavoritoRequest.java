package com.example.demo.dto.favorito;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO de entrada: lo que el cliente envía al crear o actualizar un
 * favorito. @NotNull en productoId y listaId (son Long, no tienen
 * concepto de "vacío") y @NotBlank en nota (cubre null, "" y espacios).
 */
public record FavoritoRequest(
        @NotNull(message = "productoId es obligatorio")
        Long productoId,

        @NotNull(message = "listaId es obligatorio")
        Long listaId,

        @NotBlank(message = "nota no puede estar vacía")
        String nota
) {
}