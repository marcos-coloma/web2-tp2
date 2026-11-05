package com.example.demo.service;

import com.example.demo.domain.Usuario;
import com.example.demo.dto.auth.UsuarioRegistroRequest;
import com.example.demo.dto.auth.UsuarioRegistroResponse;
import com.example.demo.exception.ConflictoRecursoException;
import com.example.demo.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Implementa la lógica de autenticación y registro de usuarios.
 * Verifica la existencia del email, hashea la contraseña y asigna
 * el rol por defecto antes de persistir el usuario.
 */
@Service
public class AuthServiceImpl implements AuthService {

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(
            UsuarioRepository repository,
            PasswordEncoder passwordEncoder
    ) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
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
}