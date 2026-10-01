package com.example.demo.controller;

import com.example.demo.dto.favorite.FavoriteInputDTO;
import com.example.demo.dto.favorite.FavoriteOutputDTO;
import com.example.demo.service.FavoriteService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @Operation(summary = "Get all favorites")
    @GetMapping
    public ResponseEntity<List<FavoriteOutputDTO>> getAll() {
        return ResponseEntity.ok(favoriteService.getAll());
    }

    @Operation(summary = "Get a favorite by ID")
    @GetMapping("/{id}")
    public ResponseEntity<FavoriteOutputDTO> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(favoriteService.getById(id));
    }

    @Operation(summary = "Create a favorite")
    @PostMapping
    public ResponseEntity<FavoriteOutputDTO> create(
            @Valid @RequestBody FavoriteInputDTO dto) {

        FavoriteOutputDTO favorite = favoriteService.create(dto);

        return ResponseEntity
                .created(URI.create("/api/favorites/" + favorite.id()))
                .body(favorite);
    }

    @Operation(summary = "Update a favorite")
    @PutMapping("/{id}")
    public ResponseEntity<FavoriteOutputDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody FavoriteInputDTO dto) {

        return ResponseEntity.ok(favoriteService.update(id, dto));
    }

    @Operation(summary = "Delete a favorite")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {

        favoriteService.delete(id);

        return ResponseEntity.noContent().build();
    }
}