
package com.example.demo.controller;

import com.example.demo.domain.Usuario;
import com.example.demo.entity.ListaEntity;
import com.example.demo.entity.UsuarioEntity;
import com.example.demo.repository.ListaJpaRepository;
import com.example.demo.repository.UsuarioJpaRepository;
import com.example.demo.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Pruebas de integración de ListaController.
 *
 * Verifica la autenticación y los permisos de acceso a las listas
 * utilizando la aplicación Spring completa y una base de datos
 * PostgreSQL real mediante Testcontainers.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class ListaControllerIT {

    private static final String PASSWORD = "Password123";

    @Container
    private static final PostgreSQLContainer postgres =
            new PostgreSQLContainer("postgres:17");

    @DynamicPropertySource
    static void configurarBaseDeDatos(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);

        registry.add(
                "jwt.secret",
                () -> "VGhpc0lzQVNlY3JldEtleUZvclRlc3RpbmdQdXJwb3Nl"
        );
        registry.add("jwt.expiration-ms", () -> "3600000");
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioJpaRepository usuarioRepository;

    @Autowired
    private ListaJpaRepository listaRepository;

    @Autowired
    private JwtService jwtService;

    private final BCryptPasswordEncoder passwordEncoder =
            new BCryptPasswordEncoder();

    private UsuarioEntity usuario;
    private UsuarioEntity otroUsuario;
    private UsuarioEntity administrador;

    private ListaEntity listaPropia;
    private ListaEntity listaAjena;

    @BeforeEach
    void setUp() {

        listaRepository.deleteAll();
        usuarioRepository.deleteAll();

        String passwordHash = passwordEncoder.encode(PASSWORD);

        usuario = usuarioRepository.save(
                new UsuarioEntity(
                        null,
                        "usuario@test.com",
                        passwordHash,
                        "USER"
                )
        );

        otroUsuario = usuarioRepository.save(
                new UsuarioEntity(
                        null,
                        "otro@test.com",
                        passwordHash,
                        "USER"
                )
        );

        administrador = usuarioRepository.save(
                new UsuarioEntity(
                        null,
                        "admin@test.com",
                        passwordHash,
                        "ADMIN"
                )
        );

        listaPropia = listaRepository.save(
                new ListaEntity(null, "Mi lista", usuario)
        );

        listaAjena = listaRepository.save(
                new ListaEntity(null, "Lista de otro usuario", otroUsuario)
        );
    }

    @Test
    void login_credencialesValidas_devuelveToken() throws Exception {

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "email": "usuario@test.com",
                                    "password": "Password123"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.tokenType").value("Bearer"));
    }

    @Test
    void login_credencialesInvalidas_devuelve401() throws Exception {

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "email": "usuario@test.com",
                                    "password": "Incorrecta123"
                                }
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void obtener_listaPropia_devuelve200() throws Exception {

        String token = generarToken(usuario);

        mockMvc.perform(get("/api/listas/" + listaPropia.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void obtener_listaAjena_devuelve403() throws Exception {

        String token = generarToken(usuario);

        mockMvc.perform(get("/api/listas/" + listaAjena.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void listar_sinToken_devuelve401() throws Exception {

        mockMvc.perform(get("/api/listas"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void obtener_listaAjenaComoAdmin_devuelve200() throws Exception {

        String token = generarToken(administrador);

        mockMvc.perform(get("/api/listas/" + listaAjena.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    private String generarToken(UsuarioEntity usuario) {

        Usuario usuarioDomain = new Usuario(
                usuario.getId(),
                usuario.getEmail(),
                usuario.getPasswordHash(),
                usuario.getRol()
        );

        return jwtService.generarToken(usuarioDomain);
    }
}
