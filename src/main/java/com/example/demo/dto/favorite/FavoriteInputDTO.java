package com.example.demo.dto.favorite;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record FavoriteInputDTO(
        @NotNull(message = "Product ID is required")
        Long productId,

        @NotBlank(message = "Note is required")
        String note
) {}