package com.example.demo.repository;

import com.example.demo.entity.FavoritoEntity;
import org.springframework.stereotype.Repository;
import com.example.demo.domain.Favorito;
import java.util.List;
import java.util.Optional;

@Repository
public class FavoritoRepositoryAdapter implements FavoritoRepository {

    private final FavoritoJpaRepository jpaRepository;

    public FavoritoRepositoryAdapter(FavoritoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<Favorito> findAll() {
        return jpaRepository.findAll()
                .stream()
                .map(entity -> new Favorito(
                        entity.getId(),
                        entity.getProductoId(),
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
                        entity.getNota(),
                        entity.getFechaAgregado()
                ));
    }

    @Override
    public Favorito save(Favorito favorito) {
        FavoritoEntity entity = new FavoritoEntity(
                favorito.id(),
                favorito.productoId(),
                favorito.nota(),
                favorito.fechaAgregado()
        );

        FavoritoEntity saved = jpaRepository.save(entity);

        return new Favorito(
                saved.getId(),
                saved.getProductoId(),
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