package com.example.demo.service;

import com.example.demo.domain.Favorito;
import com.example.demo.domain.Lista;
import com.example.demo.exception.ConflictoRecursoException;
import com.example.demo.exception.RecursoNoEncontradoException;
import com.example.demo.repository.FavoritoRepository;
import com.example.demo.repository.ListaRepository;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * Implementa la lógica de negocio de las listas de favoritos.
 * Utiliza los repositorios mediante sus interfaces y no depende directamente
 * de la implementación de persistencia.
 */
@Service
public class ListaServiceImpl implements ListaService {

    private final ListaRepository listaRepository;
    private final FavoritoRepository favoritoRepository;

    public ListaServiceImpl(
            ListaRepository listaRepository,
            FavoritoRepository favoritoRepository
    ) {
        this.listaRepository = listaRepository;
        this.favoritoRepository = favoritoRepository;
    }

    @Override
    public Lista crear(String nombre) {
        Lista nueva = new Lista(null, nombre);
        return listaRepository.save(nueva);
    }

    @Override
    public List<Lista> listar() {
        return listaRepository.findAll();
    }

    @Override
    public Lista obtener(Long id) {
        return listaRepository.findById(id)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "No existe la lista con id " + id
                        )
                );
    }

    @Override
    public List<Favorito> listarFavoritos(Long id) {
        obtener(id);

        return favoritoRepository.findAll()
                .stream()
                .filter(favorito -> favorito.listaId().equals(id))
                .toList();
    }

    @Override
    public void eliminar(Long id) {
        obtener(id);

        boolean tieneFavoritos = favoritoRepository.findAll()
                .stream()
                .anyMatch(favorito -> favorito.listaId().equals(id));

        if (tieneFavoritos) {
            throw new ConflictoRecursoException(
                    "No se puede eliminar la lista porque todavía tiene favoritos"
            );
        }

        listaRepository.deleteById(id);
    }
}