package com.example.demo.domain;

import java.time.LocalDateTime;

public record Favorite(
        Long id,
        Long productId,
        String note,
        LocalDateTime dateAdded
) {}