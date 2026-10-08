
package com.example.demo.controller;

import com.example.demo.domain.Favorito;
import com.example.demo.domain.Lista;
import com.example.demo.dto.lista.MoverFavoritosRequest;
import com.example.demo.service.ListaService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

/**
 * Expone los endpoints de las listas de favoritos.
 * Recibe las solicitudes HTTP y delega la lógica de negocio en ListaService.
 */
@RestController
@RequestMapping("/api/listas")
public class ListaController {

    private final ListaService service;

    public ListaController(ListaService service) {
        this.service = service;
    }

    @Operation(summary = "Crear una lista de favoritos")
    @PostMapping
    public ResponseEntity<Lista> crear(@RequestBody Lista lista) {
        Lista creada = service.crear(lista.nombre());

        URI location = URI.create("/api/listas/" + creada.id());

        return ResponseEntity
                .created(location)
                .body(creada);
    }

    @Operation(summary = "Mover favoritos a otra lista y eliminar la lista de origen")
    @PostMapping("/{origenId}/mover-favoritos")
    public ResponseEntity<Void> moverFavoritos(
            @PathVariable Long origenId,
            @RequestBody MoverFavoritosRequest request
    ) {
        service.moverFavoritos(origenId, request.listaDestinoId());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Listar todas las listas de favoritos")
    @GetMapping
    public ResponseEntity<List<Lista>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @Operation(summary = "Obtener una lista por su ID")
    @GetMapping("/{id}")
    public ResponseEntity<Lista> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtener(id));
    }

    @Operation(summary = "Listar los favoritos de una lista")
    @GetMapping("/{id}/favoritos")
    public ResponseEntity<List<Favorito>> listarFavoritos(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(service.listarFavoritos(id));
    }

    @Operation(summary = "Eliminar una lista de favoritos")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
