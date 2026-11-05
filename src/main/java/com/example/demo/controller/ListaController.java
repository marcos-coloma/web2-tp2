package com.example.demo.controller;

import com.example.demo.domain.Favorito;
import com.example.demo.domain.Lista;
import com.example.demo.dto.lista.ListaRequest;
import com.example.demo.dto.lista.MoverFavoritosRequest;
import com.example.demo.security.UsuarioAutenticado;
import com.example.demo.service.ListaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

/**
 * Expone los endpoints de las listas de favoritos.
 * Recibe las solicitudes HTTP y delega la lógica de negocio en ListaService.
 */
@RestController
@RequestMapping("/api/listas")
@Tag(name = "listas", description = "Gestión de listas de favoritos")
public class ListaController {

    private final ListaService service;

    public ListaController(ListaService service) {
        this.service = service;
    }

    @Operation(summary = "Crear una lista de favoritos")
    @ApiResponse(
            responseCode = "201",
            description = "Lista creada correctamente",
            content = @Content(schema = @Schema(implementation = Lista.class))
    )
    @ApiResponse(
            responseCode = "400",
            description = "Datos inválidos",
            content = @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetail.class)
            )
    )
    @ApiResponse(
            responseCode = "401",
            description = "No autenticado",
            content = @Content
    )
    @PostMapping
    public ResponseEntity<Lista> crear(
            @Valid @RequestBody ListaRequest request,
            @AuthenticationPrincipal UsuarioAutenticado usuario
    ) {
        Lista creada = service.crear(request.nombre(), usuario.id());

        URI location = URI.create("/api/listas/" + creada.id());

        return ResponseEntity
                .created(location)
                .body(creada);
    }

    @Operation(summary = "Mover favoritos a otra lista y eliminar la lista de origen")
    @ApiResponse(responseCode = "204", description = "Favoritos trasladados correctamente")
    @ApiResponse(
            responseCode = "404",
            description = "Lista de origen o destino no encontrada",
            content = @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetail.class)
            )
    )
    @ApiResponse(
            responseCode = "401",
            description = "No autenticado",
            content = @Content
    )
    @PostMapping("/{origenId}/mover-favoritos")
    public ResponseEntity<Void> moverFavoritos(
            @PathVariable Long origenId,
            @RequestBody MoverFavoritosRequest request
    ) {
        service.moverFavoritos(origenId, request.listaDestinoId());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Listar todas las listas de favoritos")
    @ApiResponse(responseCode = "200", description = "Listas obtenidas correctamente")
    @ApiResponse(
            responseCode = "401",
            description = "No autenticado",
            content = @Content
    )
    @GetMapping
    public ResponseEntity<List<Lista>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @Operation(summary = "Obtener una lista por su ID")
    @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente")
    @ApiResponse(
            responseCode = "404",
            description = "Lista no encontrada",
            content = @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetail.class)
            )
    )
    @ApiResponse(
            responseCode = "401",
            description = "No autenticado",
            content = @Content
    )
    @GetMapping("/{id}")
    public ResponseEntity<Lista> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtener(id));
    }

    @Operation(summary = "Listar los favoritos de una lista")
    @ApiResponse(responseCode = "200", description = "Favoritos obtenidos correctamente")
    @ApiResponse(
            responseCode = "404",
            description = "Lista no encontrada",
            content = @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetail.class)
            )
    )
    @ApiResponse(
            responseCode = "401",
            description = "No autenticado",
            content = @Content
    )
    @GetMapping("/{id}/favoritos")
    public ResponseEntity<List<Favorito>> listarFavoritos(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(service.listarFavoritos(id));
    }

    @Operation(summary = "Eliminar una lista de favoritos")
    @ApiResponse(responseCode = "204", description = "Lista eliminada correctamente")
    @ApiResponse(
            responseCode = "404",
            description = "Lista no encontrada",
            content = @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetail.class)
            )
    )
    @ApiResponse(
            responseCode = "409",
            description = "La lista todavía contiene favoritos",
            content = @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetail.class)
            )
    )
    @ApiResponse(
            responseCode = "401",
            description = "No autenticado",
            content = @Content
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}