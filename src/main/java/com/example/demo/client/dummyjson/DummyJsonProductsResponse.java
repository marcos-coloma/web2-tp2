package com.example.demo.client.dummyjson;


/**
 * Forma exacta de la respuesta de GET https://dummyjson.com/products
 * (incluye "products", "total", "skip" y "limit").
 */

import java.util.List;

public record DummyJsonProductsResponse(
        List<DummyJsonProduct> products,
        int total,
        int skip,
        int limit
) {

}