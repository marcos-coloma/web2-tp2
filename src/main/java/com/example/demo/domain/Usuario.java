package com.example.demo.domain;

/**
 * Modelo de dominio de un usuario: identificador, email, hash de contraseña
 * y rol asignado. El dominio almacena el hash de la contraseña, nunca la
 * contraseña original, y no depende de los detalles de persistencia.
 */

public record Usuario (
    Long id,
    String email,
    String passwordHash,
    String rol){
    }
