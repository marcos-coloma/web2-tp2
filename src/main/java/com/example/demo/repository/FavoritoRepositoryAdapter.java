package com.example.demo.repository;

import com.example.demo.domain.Favorito;
import com.example.demo.entity.FavoritoEntity;
import com.example.demo.entity.ListaEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Adapter que implementa el puerto FavoritoRepository usando JPA.
 * Traduce entre el dominio Favorito y la entidad FavoritoEntity.
 */
@Repository
public class FavoritoRepositoryAdapter implements FavoritoRepository {

    private final FavoritoJpaRepository jpaRepository;
    private final ListaJpaRepository listaJpaRepository;

    public FavoritoRepositoryAdapter(
            FavoritoJpaRepository jpaRepository,
            ListaJpaRepository listaJpaRepository
    ) {
        this.jpaRepository = jpaRepository;
        this.listaJpaRepository = listaJpaRepository;
    }

    @Override
    public List<Favorito> findAll() {
        return jpaRepository.findAll()
                .stream()
                .map(entity -> new Favorito(
                        entity.getId(),
                        entity.getProductoId(),
                        entity.getLista().getId(),
                        entity.getNota(),
                        entity.getFechaAgregado()
                ))
                .toList();
    }

    @Override
    public Optional<Favorito> findById(Long id) {
        return jpaRepository.findById(id)
                .map(entity -> new Favorito(
                        entity.getId(),
                        entity.getProductoId(),
                        entity.getLista().getId(),
                        entity.getNota(),
                        entity.getFechaAgregado()
                ));
    }

    @Override
    public Favorito save(Favorito favorito) {
        ListaEntity lista = listaJpaRepository.findById(favorito.listaId())
                .orElseThrow();

        FavoritoEntity entity = new FavoritoEntity(
                favorito.id(),
                favorito.productoId(),
                favorito.nota(),
                favorito.fechaAgregado(),
                lista
        );

        FavoritoEntity saved = jpaRepository.save(entity);

        return new Favorito(
                saved.getId(),
                saved.getProductoId(),
                saved.getLista().getId(),
                saved.getNota(),
                saved.getFechaAgregado()
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