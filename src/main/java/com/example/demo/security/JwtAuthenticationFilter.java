package com.example.demo.security;

import com.example.demo.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Filtro de autenticación JWT que intercepta las peticiones HTTP.
 * Extrae y valida el token Bearer, y establece la identidad del usuario
 * en el contexto de seguridad de Spring.
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authorization == null
                || !authorization.regionMatches(true, 0, "Bearer ", 0, 7)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authorization.substring(7).trim();

        try {
            Claims claims = jwtService.validarYObtenerClaims(token);

            String email = claims.getSubject();
            String rol = claims.get("rol", String.class);
            Object idClaim = claims.get("id");

            if (email == null || email.isBlank()
                    || rol == null || rol.isBlank()
                    || !(idClaim instanceof Number id)) {
                response.sendError(
                        HttpServletResponse.SC_UNAUTHORIZED,
                        "Token inválido"
                );
                return;
            }

            UsuarioAutenticado usuario = new UsuarioAutenticado(
                    id.longValue(),
                    email,
                    rol
            );

            var authentication = new UsernamePasswordAuthenticationToken(
                    usuario,
                    null,
                    List.of(new SimpleGrantedAuthority("ROLE_" + rol))
            );

            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);

        } catch (JwtException | IllegalArgumentException _) {
            SecurityContextHolder.clearContext();

            response.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Token inválido o vencido"
            );
            return;
        }

        filterChain.doFilter(request, response);
    }
}