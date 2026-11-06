package com.example.demo.service;

import com.example.demo.domain.Usuario;
import com.example.demo.dto.auth.UsuarioRegistroRequest;
import com.example.demo.dto.auth.UsuarioRegistroResponse;
import com.example.demo.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Pruebas unitarias de AuthServiceImpl.
 *
 * Verifica que el registro de usuarios almacene las contraseñas
 * mediante un hash BCrypt y nunca en texto plano.
 *
 * Utiliza Mockito para simular las dependencias del servicio
 * y JUnit para comprobar los resultados.
 */


@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private JwtService jwtService;

    private PasswordEncoder passwordEncoder;
    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
        authService = new AuthServiceImpl(
                usuarioRepository,
                passwordEncoder,
                jwtService
        );
    }

    @Test
    void registrar_contrasenaValida_guardaHashYNoTextoPlano() {
        String email = "usuario@test.com";
        String password = "MiPassword123";

        when(usuarioRepository.existsByEmail(email)).thenReturn(false);

        when(usuarioRepository.save(any(Usuario.class)))
                .thenAnswer(invocation -> {
                    Usuario usuario = invocation.getArgument(0);
                    return new Usuario(
                            1L,
                            usuario.email(),
                            usuario.passwordHash(),
                            usuario.rol()
                    );
                });

        UsuarioRegistroRequest request =
                new UsuarioRegistroRequest(email, password);

        UsuarioRegistroResponse response = authService.registrar(request);

        ArgumentCaptor<Usuario> usuarioCaptor =
                ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(usuarioCaptor.capture());

        Usuario usuarioGuardado = usuarioCaptor.getValue();

        assertNotEquals(password, usuarioGuardado.passwordHash());
        assertTrue(passwordEncoder.matches(
                password,
                usuarioGuardado.passwordHash()
        ));

        assertEquals(email, usuarioGuardado.email());
        assertEquals("USER", usuarioGuardado.rol());
        assertEquals(1L, response.id());
        assertEquals(email, response.email());
    }
}