package com.example.demo.service;

import com.example.demo.domain.Favorito;
import com.example.demo.domain.Lista;

import java.util.List;

/**
 * Contrato de la lógica de negocio de las listas de favoritos.
 * El controller depende de esta interfaz, no de ListaServiceImpl, para mantener
 * separada la lógica de negocio de la implementación concreta.
 */

public interface ListaService {
    Lista crear(String nombre, Long usuarioId);
    List<Lista> listar(Long usuarioId, String rol);
    Lista obtener(Long id, Long usuarioId, String rol);
    List<Favorito> listarFavoritos(Long id, Long usuarioId, String rol);
    void moverFavoritos(Long origenId, Long destinoId, Long usuarioId, String rol);
    void eliminar(Long id, Long usuarioId, String rol);
}