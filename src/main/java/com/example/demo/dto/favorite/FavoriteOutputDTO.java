package com.example.demo.dto.favorite;

import java.time.LocalDateTime;

public record FavoriteOutputDTO(
        Long id,
        Long productId,
        String note,
        LocalDateTime dateAdded
) {}