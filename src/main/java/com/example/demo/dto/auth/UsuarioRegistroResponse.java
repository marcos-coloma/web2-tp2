package com.example.demo.dto.auth;

/**
 * DTO de salida del registro de un usuario.
 * Expone sus datos públicos sin incluir información sensible
 * como el hash de la contraseña.
 */
public record UsuarioRegistroResponse(
        Long id,
        String email,
        String rol
) {
}