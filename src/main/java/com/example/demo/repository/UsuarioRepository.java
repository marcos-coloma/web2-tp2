package com.example.demo.repository;

import com.example.demo.domain.Usuario;

import java.util.Optional;

/**
 * Contrato de acceso a datos para usuarios. El service depende de esta
 * interfaz y no de la implementación concreta, permitiendo mantener separada
 * la lógica de negocio de la persistencia.
 */
public interface UsuarioRepository {

    Optional<Usuario> findByEmail(String email);
    boolean existsByEmail(String email);
    Usuario save(Usuario usuario);
}