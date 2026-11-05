package com.example.demo.service;

import com.example.demo.domain.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;

/**
 * Servicio encargado de generar y validar tokens JWT.
 * Incluye la identidad y el rol del usuario, y verifica la firma
 * y la expiración de los tokens recibidos.
 */
@Service
public class JwtService {

    private final SecretKey signingKey;
    private final JwtParser jwtParser;
    private final long expirationMs;

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-ms}") long expirationMs
    ) {
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("JWT_SECRET no está configurado");
        }

        if (expirationMs <= 0) {
            throw new IllegalArgumentException("La expiración del JWT debe ser positiva");
        }

        byte[] keyBytes = Decoders.BASE64.decode(secret);

        if (keyBytes.length < 32) {
            throw new IllegalArgumentException(
                    "JWT_SECRET debe contener al menos 32 bytes para HS256"
            );
        }

        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
        this.expirationMs = expirationMs;

        this.jwtParser = Jwts.parser()
                .verifyWith(signingKey)
                .build();
    }

    public String generarToken(Usuario usuario) {

        Instant ahora = Instant.now();

        return Jwts.builder()
                .subject(usuario.email())
                .claim("id", usuario.id())
                .claim("rol", usuario.rol())
                .issuedAt(Date.from(ahora))
                .expiration(Date.from(ahora.plusMillis(expirationMs)))
                .signWith(signingKey, Jwts.SIG.HS256)
                .compact();
    }

    public Claims validarYObtenerClaims(String token) {
        return jwtParser.parseSignedClaims(token).getPayload();
    }
}