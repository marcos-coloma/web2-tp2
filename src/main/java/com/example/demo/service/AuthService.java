package com.example.demo.service;

import com.example.demo.dto.auth.UsuarioLoginRequest;
import com.example.demo.dto.auth.UsuarioLoginResponse;
import com.example.demo.dto.auth.UsuarioRegistroRequest;
import com.example.demo.dto.auth.UsuarioRegistroResponse;

/**
 * Contrato de negocio para las operaciones de autenticación.
 * Define las operaciones disponibles sin exponer detalles de implementación.
 */
public interface AuthService {

    UsuarioRegistroResponse registrar(UsuarioRegistroRequest request);

    UsuarioLoginResponse login(UsuarioLoginRequest request);
}