package com.example.demo.repository;

import com.example.demo.domain.Favorito;

import java.util.List;
import java.util.Optional;


/**
 * Contrato de acceso a datos para favoritos.
 * El service depende de esta interfaz y no de la implementación concreta,
 * permitiendo mantener separada la lógica de negocio de la persistencia.
 */

public interface FavoritoRepository {
    List<Favorito> findAll();
    Optional<Favorito> findById(Long id);
    List<Favorito> findByListaId(Long listaId);
    Favorito save(Favorito favorito);
    void deleteById(Long id);
    boolean existsById(Long id);
}
