package com.example.demo.service;

import com.example.demo.domain.Lista;
import com.example.demo.repository.FavoritoRepository;
import com.example.demo.repository.ListaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Pruebas unitarias de ListaServiceImpl.
 *
 * Verifica que las operaciones del servicio gestionen correctamente
 * las listas y respeten la asociación entre cada lista y su usuario.
 *
 * Utiliza Mockito para simular el repositorio y aislar el servicio
 * de la base de datos.
 */



@ExtendWith(MockitoExtension.class)
class ListaServiceImplTest {

    @Mock
    private ListaRepository listaRepository;

    @Mock
    private FavoritoRepository favoritoRepository;

    private ListaServiceImpl listaService;

    @BeforeEach
    void setUp() {
        listaService = new ListaServiceImpl(listaRepository, favoritoRepository);
    }

    @Test
    void crear_usuarioAutenticado_asociaListaAlUsuario() {
        Long usuarioId = 10L;
        String nombre = "Mis favoritos";

        when(listaRepository.save(any(Lista.class)))
                .thenAnswer(invocation -> {
                    Lista lista = invocation.getArgument(0);
                    return new Lista(1L, lista.nombre(), lista.usuarioId());
                });

        Lista resultado = listaService.crear(nombre, usuarioId);

        ArgumentCaptor<Lista> listaCaptor = ArgumentCaptor.forClass(Lista.class);
        verify(listaRepository).save(listaCaptor.capture());

        Lista listaGuardada = listaCaptor.getValue();

        assertEquals(usuarioId, listaGuardada.usuarioId());
        assertEquals(usuarioId, resultado.usuarioId());
        assertEquals(nombre, resultado.nombre());
        assertNotNull(resultado.id());
    }
}