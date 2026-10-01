package com.example.demo.repository.favorite;

import com.example.demo.domain.Favorite;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class InMemoryFavoriteRepository implements FavoriteRepository {

    private final List<Favorite> favorites = new ArrayList<>();
    private long nextId = 1;

    @Override
    public List<Favorite> findAll() {
        return favorites;
    }

    @Override
    public Optional<Favorite> findById(Long id) {
        return favorites.stream()
                .filter(favorite -> favorite.id().equals(id))
                .findFirst();
    }

    @Override
    public Favorite save(Favorite favorite) {

        if (favorite.id() == null) {
            Favorite newFavorite = new Favorite(
                    nextId++,
                    favorite.productId(),
                    favorite.note(),
                    favorite.dateAdded()
            );

            favorites.add(newFavorite);
            return newFavorite;
        }

        favorites.removeIf(f -> f.id().equals(favorite.id()));
        favorites.add(favorite);

        return favorite;
    }

    @Override
    public void deleteById(Long id) {
        favorites.removeIf(favorite -> favorite.id().equals(id));
    }
}