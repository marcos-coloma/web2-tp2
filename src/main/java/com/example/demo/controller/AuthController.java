package com.example.demo.controller;

import com.example.demo.dto.auth.UsuarioRegistroRequest;
import com.example.demo.dto.auth.UsuarioRegistroResponse;
import com.example.demo.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller encargado de gestionar las operaciones de autenticación
 * de usuarios mediante endpoints HTTP.
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "autenticación", description = "Registro y autenticación de usuarios")
public class AuthController {

    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    @PostMapping("/registro")
    @Operation(summary = "Registrar un usuario")
    public ResponseEntity<UsuarioRegistroResponse> registrar(
            @Valid @RequestBody UsuarioRegistroRequest request
    ) {
        UsuarioRegistroResponse response = service.registrar(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}