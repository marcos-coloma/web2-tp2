package com.example.demo.repository;

import com.example.demo.domain.Usuario;
import com.example.demo.entity.UsuarioEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Adapter que implementa el puerto UsuarioRepository usando JPA.
 * Traduce entre el dominio Usuario y la entidad UsuarioEntity.
 */
@Repository
public class UsuarioRepositoryAdapter implements UsuarioRepository {

    private final UsuarioJpaRepository jpaRepository;

    public UsuarioRepositoryAdapter(UsuarioJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<Usuario> findByEmail(String email) {
        return jpaRepository.findByEmail(email)
                .map(this::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }

    @Override
    public Usuario save(Usuario usuario) {
        UsuarioEntity entity = new UsuarioEntity(
                usuario.id(),
                usuario.email(),
                usuario.passwordHash(),
                usuario.rol()
        );

        UsuarioEntity saved = jpaRepository.save(entity);

        return toDomain(saved);
    }

    private Usuario toDomain(UsuarioEntity entity) {
        return new Usuario(
                entity.getId(),
                entity.getEmail(),
                entity.getPasswordHash(),
                entity.getRol()
        );
    }
}