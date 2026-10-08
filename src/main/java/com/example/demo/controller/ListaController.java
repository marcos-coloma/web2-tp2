package com.example.demo.controller;

import com.example.demo.domain.Favorito;
import com.example.demo.domain.Lista;
import com.example.demo.dto.lista.MoverFavoritosRequest;
import com.example.demo.service.ListaService;
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

    @PostMapping
    public ResponseEntity<Lista> crear(@RequestBody Lista lista) {
        Lista creada = service.crear(lista.nombre());

        URI location = URI.create("/api/listas/" + creada.id());

        return ResponseEntity
                .created(location)
                .body(creada);
    }

    @PostMapping("/{origenId}/mover-favoritos")
    public ResponseEntity<Void> moverFavoritos(
            @PathVariable Long origenId,
            @RequestBody MoverFavoritosRequest request
    ) {
        service.moverFavoritos(origenId, request.listaDestinoId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<Lista>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Lista> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtener(id));
    }

    @GetMapping("/{id}/favoritos")
    public ResponseEntity<List<Favorito>> listarFavoritos(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(service.listarFavoritos(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}