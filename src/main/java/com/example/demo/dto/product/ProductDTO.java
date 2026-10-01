package com.example.demo.dto.product;

public record ProductDTO(
        Long id,
        String name,
        String description,
        String category,
        double price,
        int stock,
        String image
) {}