package com.example.demo.repository;

import com.example.demo.domain.Lista;

import java.util.List;
import java.util.Optional;

/**
 * Contrato de acceso a datos para listas. El service depende de esta
 * interfaz, no de la implementación concreta — puede cambiarse entre
 * diferentes formas de persistencia sin tocar el service.
 */

public interface ListaRepository {

    List<Lista> findAll();
    Optional<Lista> findById(Long id);
    Lista save(Lista lista);
    void deleteById(Long id);
    boolean existsById(Long id);
}