package com.example.demo.controller;

import com.example.demo.dto.favorito.FavoritoRequest;
import com.example.demo.dto.favorito.FavoritoResponse;
import com.example.demo.service.FavoritoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

/**
 * Controller encargado de gestionar las operaciones CRUD de favoritos.
 * Todos los endpoints requieren autenticación mediante JWT.
 */
@RestController
@RequestMapping("/api/favoritos")
@Tag(
        name = "favoritos",
        description = "Gestión de favoritos del usuario. Requiere autenticación JWT."
)
@SecurityRequirement(name = "bearerAuth")
public class FavoritoController {

    private final FavoritoService service;

    public FavoritoController(FavoritoService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar favoritos")
    @ApiResponse(responseCode = "200", description = "Favoritos obtenidos correctamente")
    @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content)
    public List<FavoritoResponse> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un favorito por id")
    @ApiResponse(responseCode = "200", description = "Favorito obtenido correctamente")
    @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content)
    @ApiResponse(
            responseCode = "404",
            description = "Favorito no encontrado",
            content = @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetail.class)
            )
    )
    public FavoritoResponse obtener(@PathVariable Long id) {
        return service.obtener(id);
    }

    @PostMapping
    @Operation(summary = "Crear un favorito")
    @ApiResponse(responseCode = "201", description = "Favorito creado correctamente")
    @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content)
    @ApiResponse(
            responseCode = "400",
            description = "Datos inválidos",
            content = @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetail.class)
            )
    )
    public ResponseEntity<FavoritoResponse> crear(
            @Valid @RequestBody FavoritoRequest request
    ) {
        FavoritoResponse creado = service.crear(request);
        return ResponseEntity.created(URI.create("/api/favoritos/" + creado.id())).body(creado);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un favorito")
    @ApiResponse(responseCode = "200", description = "Favorito actualizado correctamente")
    @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content)
    @ApiResponse(
            responseCode = "400",
            description = "Datos inválidos",
            content = @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetail.class)
            )
    )
    @ApiResponse(
            responseCode = "404",
            description = "Favorito no encontrado",
            content = @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetail.class)
            )
    )
    public FavoritoResponse actualizar(
            @PathVariable Long id,
            @Valid @RequestBody FavoritoRequest request
    ) {
        return service.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un favorito")
    @ApiResponse(responseCode = "204", description = "Favorito eliminado correctamente")
    @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content)
    @ApiResponse(
            responseCode = "404",
            description = "Favorito no encontrado",
            content = @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetail.class)
            )
    )
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}