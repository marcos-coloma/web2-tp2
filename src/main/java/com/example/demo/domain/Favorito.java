package com.example.demo.domain;

import java.time.LocalDateTime;

/**
  * Modelo de dominio de un favorito: referencia a un producto y a una lista
  * mediante sus ids, más una nota personal. El dominio no guarda los objetos
  * completos, sino sus identificadores, mientras que la relación con la lista
  * se representa como clave foránea en la base de datos.
  */
public record Favorito(
        Long id,
        Long productoId,
        Long listaId,
        String nota,
        LocalDateTime fechaAgregado
) {
}