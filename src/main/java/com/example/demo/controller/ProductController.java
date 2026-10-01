package com.example.demo.controller;

import com.example.demo.dto.product.ProductDTO;
import com.example.demo.dto.product.PaginatedProductsDTO;
import com.example.demo.service.ProductService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.Min;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @Operation(summary = "Get all products")
    @GetMapping
    public PaginatedProductsDTO getProducts(
            @RequestParam(defaultValue = "10") @Min(1) int limit,
            @RequestParam(defaultValue = "0") @Min(0) int skip) {

        return productService.getProducts(limit, skip);
    }

    @Operation(summary = "Get a product by ID")
    @GetMapping("/{id}")
    public ProductDTO getProductById(@PathVariable Long id) {
        return productService.getProductById(id);
    }
}