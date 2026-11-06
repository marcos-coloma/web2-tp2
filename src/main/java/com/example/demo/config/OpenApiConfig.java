package com.example.demo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Configura la información general de la API que aparece en Swagger UI,
 * incluyendo su título, descripción y versión.
 */

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI demoOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Web II - API de gestión de favoritos")
                        .description("API REST para gestionar productos, favoritos, listas y usuarios.")
                        .version("v1"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                        )
                );
    }
}