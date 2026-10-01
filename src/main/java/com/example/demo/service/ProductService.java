package com.example.demo.service;

import com.example.demo.client.dummyjson.DummyJsonClient;
import com.example.demo.client.dummyjson.DummyJsonProduct;
import com.example.demo.client.dummyjson.DummyJsonProductsResponse;
import com.example.demo.dto.product.ProductDTO;
import com.example.demo.dto.product.PaginatedProductsDTO;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final DummyJsonClient dummyJsonClient;

    public ProductService(DummyJsonClient dummyJsonClient) {
        this.dummyJsonClient = dummyJsonClient;
    }

    public List<ProductDTO> getProducts() {
        return dummyJsonClient.getProducts()
                .products()
                .stream()
                .map(this::mapProduct)
                .toList();
    }

    public ProductDTO getProductById(Long id) {
        DummyJsonProduct product = dummyJsonClient.getProductById(id);

        return mapProduct(product);
    }

    public PaginatedProductsDTO getProducts(int limit, int skip) {
        DummyJsonProductsResponse response =
                dummyJsonClient.getProducts(limit, skip);

        List<ProductDTO> products = response.products()
                .stream()
                .map(this::mapProduct)
                .toList();

        return new PaginatedProductsDTO(
                products,
                response.total(),
                response.limit(),
                response.skip()
        );
    }

    private ProductDTO mapProduct(DummyJsonProduct product) {
        return new ProductDTO(
                product.id(),
                product.title(),
                product.description(),
                product.category(),
                product.price(),
                product.stock(),
                product.thumbnail()
        );
    }
}