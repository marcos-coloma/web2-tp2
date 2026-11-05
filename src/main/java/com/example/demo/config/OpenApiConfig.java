package com.example.demo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
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
                        .description(
                                "API REST para consultar un catálogo de productos, "
                                + "gestionar listas y favoritos, y registrar usuarios. "
                                + "Utiliza PostgreSQL para la persistencia, "
                                + "JPA para el acceso a datos y Flyway para las migraciones."
                        )
                        .version("v1"));
    }
}