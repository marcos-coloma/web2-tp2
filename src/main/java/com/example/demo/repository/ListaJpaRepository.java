package com.example.demo.repository;

import com.example.demo.entity.ListaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio JPA para acceder a las listas persistidas en la base de datos.
 * Spring Data JPA genera automáticamente la implementación.
 */
public interface ListaJpaRepository extends JpaRepository<ListaEntity, Long> {
}