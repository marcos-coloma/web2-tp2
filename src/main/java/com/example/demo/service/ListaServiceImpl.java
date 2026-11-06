package com.example.demo.service;

import com.example.demo.domain.Favorito;
import com.example.demo.domain.Lista;
import com.example.demo.exception.ConflictoRecursoException;
import com.example.demo.exception.RecursoNoEncontradoException;
import com.example.demo.repository.FavoritoRepository;
import com.example.demo.repository.ListaRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * Implementa la lógica de negocio de las listas de favoritos.
 * Controla la propiedad de las listas antes de permitir operaciones
 * sobre recursos específicos.
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
    public Lista crear(String nombre, Long usuarioId) {
        Lista nueva = new Lista(null, nombre, usuarioId);
        return listaRepository.save(nueva);
    }

    @Override
    public List<Lista> listar(Long usuarioId, String rol) {
        if ("ADMIN".equalsIgnoreCase(rol)) {
            return listaRepository.findAll();
        }

        return listaRepository.findByUsuarioId(usuarioId);
    }

    @Override
    public Lista obtener(Long id, Long usuarioId, String rol) {
        return obtenerYVerificarPropiedad(id, usuarioId, rol);
    }

    @Override
    public List<Favorito> listarFavoritos(
            Long id,
            Long usuarioId,
            String rol
    ) {
        obtenerYVerificarPropiedad(id, usuarioId, rol);

        return favoritoRepository.findAll()
                .stream()
                .filter(favorito -> favorito.listaId().equals(id))
                .toList();
    }

    @Override
    @Transactional
    public void moverFavoritos(
            Long origenId,
            Long destinoId,
            Long usuarioId,
            String rol
    ) {
        Lista origen = obtenerYVerificarPropiedad(origenId, usuarioId, rol);
        Lista destino = obtenerYVerificarPropiedad(destinoId, usuarioId, rol);

        List<Favorito> favoritos = favoritoRepository.findByListaId(origenId);

        for (Favorito favorito : favoritos) {
            Favorito actualizado = new Favorito(
                    favorito.id(),
                    favorito.productoId(),
                    destino.id(),
                    favorito.nota(),
                    favorito.fechaAgregado()
            );

            favoritoRepository.save(actualizado);
        }

        listaRepository.deleteById(origen.id());
    }

    @Override
    public void eliminar(Long id, Long usuarioId, String rol) {
        obtenerYVerificarPropiedad(id, usuarioId, rol);

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

    private Lista obtenerYVerificarPropiedad(
            Long id,
            Long usuarioId,
            String rol
    ) {
        Lista lista = listaRepository.findById(id)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "No existe la lista con id " + id
                        )
                );

        boolean esAdmin = "ADMIN".equalsIgnoreCase(rol);
        boolean esPropietario = Objects.equals(lista.usuarioId(), usuarioId);

        if (!esAdmin && !esPropietario) {
            throw new AccessDeniedException(
                    "No tenés permiso para acceder a esta lista"
            );
        }

        return lista;
    }
}