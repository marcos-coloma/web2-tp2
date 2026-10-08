package com.example.demo.repository;

import com.example.demo.domain.Lista;
import com.example.demo.entity.ListaEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Adapter que implementa el puerto ListaRepository usando JPA.
 * Traduce entre el dominio Lista y la entidad ListaEntity.
 */
@Repository
public class ListaRepositoryAdapter implements ListaRepository {

    private final ListaJpaRepository jpaRepository;

    public ListaRepositoryAdapter(ListaJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<Lista> findAll() {
        return jpaRepository.findAll()
                .stream()
                .map(entity -> new Lista(
                        entity.getId(),
                        entity.getNombre()
                ))
                .toList();
    }

    @Override
    public Optional<Lista> findById(Long id) {
        return jpaRepository.findById(id)
                .map(entity -> new Lista(
                        entity.getId(),
                        entity.getNombre()
                ));
    }

    @Override
    public Lista save(Lista lista) {
        ListaEntity entity = new ListaEntity(
                lista.id(),
                lista.nombre()
        );

        ListaEntity saved = jpaRepository.save(entity);

        return new Lista(
                saved.getId(),
                saved.getNombre()
        );
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return jpaRepository.existsById(id);
    }
}