package com.example.demo.security;

/**
 * Representa la identidad de un usuario autenticado mediante JWT.
 * Se utiliza como principal dentro del contexto de seguridad de Spring.
 */
public record UsuarioAutenticado(
        Long id,
        String email,
        String rol
) {
}