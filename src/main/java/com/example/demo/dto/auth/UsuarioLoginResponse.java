package com.example.demo.dto.auth;

/**
 * DTO de salida del inicio de sesión.
 * Devuelve el token JWT generado y el tipo de autenticación utilizado.
 */
public record UsuarioLoginResponse(
        String accessToken,
        String tokenType
) {
}