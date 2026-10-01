package com.example.demo.service;

import com.example.demo.domain.Favorite;
import com.example.demo.dto.favorite.FavoriteInputDTO;
import com.example.demo.dto.favorite.FavoriteOutputDTO;
import com.example.demo.repository.favorite.FavoriteRepository;
import org.springframework.stereotype.Service;
import com.example.demo.exception.ResourceNotFoundException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;

    public FavoriteService(FavoriteRepository favoriteRepository) {
        this.favoriteRepository = favoriteRepository;
    }

    public FavoriteOutputDTO create(FavoriteInputDTO dto) {
        Favorite favorite = convertToDomain(dto);

        return convertToOutput(favoriteRepository.save(favorite));
    }

    public List<FavoriteOutputDTO> getAll() {
        return favoriteRepository.findAll()
                .stream()
                .map(this::convertToOutput)
                .toList();
    }

public FavoriteOutputDTO getById(Long id) {
    Favorite favorite = favoriteRepository.findById(id)
            .orElseThrow(() ->
                    new ResourceNotFoundException("Favorite not found with id: " + id));

    return convertToOutput(favorite);
}

public FavoriteOutputDTO update(Long id, FavoriteInputDTO dto) {
    Favorite currentFavorite = favoriteRepository.findById(id)
            .orElseThrow(() ->
                    new ResourceNotFoundException("Favorite not found with id: " + id));

    Favorite updatedFavorite = new Favorite(
            currentFavorite.id(),
            dto.productId(),
            dto.note(),
            currentFavorite.dateAdded()
    );

    return convertToOutput(favoriteRepository.save(updatedFavorite));
}

    public void delete(Long id) {
        favoriteRepository.deleteById(id);
    }

    private Favorite convertToDomain(FavoriteInputDTO dto) {
        return new Favorite(
                null,
                dto.productId(),
                dto.note(),
                LocalDateTime.now()
        );
    }

    private FavoriteOutputDTO convertToOutput(Favorite favorite) {
        return new FavoriteOutputDTO(
                favorite.id(),
                favorite.productId(),
                favorite.note(),
                favorite.dateAdded()
        );
    }
}