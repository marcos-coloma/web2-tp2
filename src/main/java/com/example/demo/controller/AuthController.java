package com.example.demo.controller;

import com.example.demo.dto.auth.UsuarioRegistroRequest;
import com.example.demo.dto.auth.UsuarioRegistroResponse;
import com.example.demo.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
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
    @ApiResponse(
            responseCode = "201",
            description = "Usuario registrado",
            content = @Content(
                    schema = @Schema(implementation = UsuarioRegistroResponse.class)
            )
    )
    @ApiResponse(
            responseCode = "400",
            description = "Datos inválidos",
            content = @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetail.class)
            )
    )
    @ApiResponse(
            responseCode = "409",
            description = "Email duplicado",
            content = @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetail.class)
            )
    )
    public ResponseEntity<UsuarioRegistroResponse> registrar(
            @Valid @RequestBody UsuarioRegistroRequest request
    ) {
        UsuarioRegistroResponse response = service.registrar(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}