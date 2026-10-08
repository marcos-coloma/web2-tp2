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
    Lista crear(String nombre);
    List<Lista> listar();
    Lista obtener(Long id);
    List<Favorito> listarFavoritos(Long id);
    void eliminar(Long id);
}