package com.example.demo.repository;

import com.example.demo.entity.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repositorio JPA para acceder a los usuarios persistidos en la base de datos.
 * Spring Data JPA genera automáticamente la implementación.
 */
public interface UsuarioJpaRepository
        extends JpaRepository<UsuarioEntity, Long> {

    Optional<UsuarioEntity> findByEmail(String email);

    boolean existsByEmail(String email);
}