package com.example.demo.repository;

import com.example.demo.entity.FavoritoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repositorio JPA para acceder a las listas persistidas en la base de datos.
 * Spring Data JPA genera automáticamente la implementación.
 */


public interface FavoritoJpaRepository
        extends JpaRepository<FavoritoEntity, Long> {

    List<FavoritoEntity> findByListaId(Long listaId);
}