package com.example.demo.service;

import com.example.demo.domain.Usuario;
import com.example.demo.dto.auth.UsuarioLoginRequest;
import com.example.demo.dto.auth.UsuarioLoginResponse;
import com.example.demo.dto.auth.UsuarioRegistroRequest;
import com.example.demo.dto.auth.UsuarioRegistroResponse;
import com.example.demo.exception.ConflictoRecursoException;
import com.example.demo.repository.UsuarioRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Implementa la lógica de autenticación y registro de usuarios.
 * Gestiona las credenciales, el hash de las contraseñas y la generación
 * de tokens JWT.
 */
@Service
public class AuthServiceImpl implements AuthService {

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthServiceImpl(
            UsuarioRepository repository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    public UsuarioRegistroResponse registrar(UsuarioRegistroRequest request) {

        if (repository.existsByEmail(request.email())) {
            throw new ConflictoRecursoException(
                    "Ya existe un usuario registrado con ese email"
            );
        }

        String passwordHash = passwordEncoder.encode(request.password());

        Usuario nuevo = new Usuario(
                null,
                request.email(),
                passwordHash,
                "USER"
        );

        Usuario guardado = repository.save(nuevo);

        return new UsuarioRegistroResponse(
                guardado.id(),
                guardado.email(),
                guardado.rol()
        );
    }

    @Override
    public UsuarioLoginResponse login(UsuarioLoginRequest request) {

        Usuario usuario = repository.findByEmail(request.email())
                .orElseThrow(() ->
                        new BadCredentialsException("Credenciales inválidas")
                );

        if (!passwordEncoder.matches(
                request.password(),
                usuario.passwordHash()
        )) {
            throw new BadCredentialsException("Credenciales inválidas");
        }

        String token = jwtService.generarToken(usuario);

        return new UsuarioLoginResponse(token, "Bearer");
    }
}