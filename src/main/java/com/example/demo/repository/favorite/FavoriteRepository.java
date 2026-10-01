package com.example.demo.repository.favorite;

import com.example.demo.domain.Favorite;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository {

    List<Favorite> findAll();

    Optional<Favorite> findById(Long id);

    Favorite save(Favorite favorite);

    void deleteById(Long id);
}