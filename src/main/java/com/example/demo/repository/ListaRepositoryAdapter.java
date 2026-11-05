package com.example.demo.repository;

import com.example.demo.domain.Lista;
import com.example.demo.entity.ListaEntity;
import com.example.demo.entity.UsuarioEntity;
import com.example.demo.exception.RecursoNoEncontradoException;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ListaRepositoryAdapter implements ListaRepository {

    private final ListaJpaRepository jpaRepository;
    private final UsuarioJpaRepository usuarioJpaRepository;

    public ListaRepositoryAdapter(
            ListaJpaRepository jpaRepository,
            UsuarioJpaRepository usuarioJpaRepository
    ) {
        this.jpaRepository = jpaRepository;
        this.usuarioJpaRepository = usuarioJpaRepository;
    }

    @Override
    public List<Lista> findAll() {
        return jpaRepository.findAll()
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Optional<Lista> findById(Long id) {
        return jpaRepository.findById(id)
                .map(this::toDomain);
    }

    @Override
    public Lista save(Lista lista) {
        UsuarioEntity usuario = lista.usuarioId() == null
                ? null
                : usuarioJpaRepository.findById(lista.usuarioId())
                        .orElseThrow(() ->
                                new RecursoNoEncontradoException(
                                        "No existe el usuario con id " + lista.usuarioId()
                                )
                        );

        ListaEntity entity = new ListaEntity(
                lista.id(),
                lista.nombre(),
                usuario
        );

        return toDomain(jpaRepository.save(entity));
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return jpaRepository.existsById(id);
    }

    private Lista toDomain(ListaEntity entity) {
        Long usuarioId = entity.getUsuario() == null
                ? null
                : entity.getUsuario().getId();

        return new Lista(
                entity.getId(),
                entity.getNombre(),
                usuarioId
        );
    }
}