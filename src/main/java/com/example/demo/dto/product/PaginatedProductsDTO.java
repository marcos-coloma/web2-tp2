package com.example.demo.dto.product;

import java.util.List;

public record PaginatedProductsDTO(
        List<ProductDTO> products,
        int total,
        int limit,
        int skip
) {}