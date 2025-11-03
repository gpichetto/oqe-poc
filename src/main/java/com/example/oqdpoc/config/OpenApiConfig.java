package com.example.oqdpoc.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.media.*;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;

import java.util.Collections;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "apiKey";

        return new OpenAPI()
                .info(new Info()
                        .title("PDF Generation API")
                        .version("1.0")
                        .description("API for generating PDF documents from job tickets and images"))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name("X-API-KEY")
                                        .type(SecurityScheme.Type.APIKEY)
                                        .in(SecurityScheme.In.HEADER)
                                        .description("API Key for authentication"))
                        .addSchemas("ErrorResponse", new ObjectSchema()
                                .addProperty("timestamp", new StringSchema().example("2025-10-20T14:30:00Z"))
                                .addProperty("status", new IntegerSchema().example(400))
                                .addProperty("error", new StringSchema().example("Bad Request"))
                                .addProperty("message", new StringSchema().example("Error description"))
                                .addProperty("path", new StringSchema().example("/api/endpoint"))))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName));
    }

    private ApiResponse createErrorResponse(String description) {
        return new ApiResponse()
                .description(description)
                .content(new Content()
                        .addMediaType(MediaType.APPLICATION_JSON_VALUE,
                                new io.swagger.v3.oas.models.media.MediaType()
                                        .schema(new ObjectSchema()
                                                .addProperty("timestamp", new StringSchema().example("2025-10-29T15:05:00Z"))
                                                .addProperty("status", new IntegerSchema().example(400))
                                                .addProperty("error", new StringSchema().example("Bad Request"))
                                                .addProperty("message", new StringSchema().example(description))
                                                .addProperty("path", new StringSchema().example("/api/endpoint")))));
    }
}