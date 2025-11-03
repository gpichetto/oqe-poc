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

    @Bean
    public OpenApiCustomizer openApiCustomizer() {
        return openApi -> {
            // Define the PDF generation endpoint
            Operation pdfOperation = new Operation()
                    .tags(Collections.singletonList("PDF Generation"))
                    .summary("Generate PDF from job ticket with images")
                    .description("Generates a PDF document from a job ticket JSON and optional images. " +
                            "Supports both JSON response with base64-encoded PDF or direct PDF download.")
                    .addParametersItem(new io.swagger.v3.oas.models.parameters.Parameter()
                            .name("Accept")
                            .in("header")
                            .schema(new StringSchema()
                                    .addEnumItem(MediaType.APPLICATION_JSON_VALUE)
                                    .addEnumItem(MediaType.APPLICATION_PDF_VALUE))
                            .description("Response type (defaults to application/json)"))
                    .requestBody(new RequestBody()
                            .content(new Content()
                                    .addMediaType(MediaType.MULTIPART_FORM_DATA_VALUE,
                                            new io.swagger.v3.oas.models.media.MediaType()
                                                    .schema(new ObjectSchema()
                                                            .addProperty("jobTicket", new StringSchema()
                                                                    .description("JSON string containing job ticket data")
                                                                    .example("{\"checklistId\": \"12345\", \"title\": \"Job Ticket\"}"))
                                                            .addProperty("imageFiles", new ArraySchema()
                                                                    .items(new Schema<Object>().type("string").format("binary"))
                                                                    .description("Array of image files to include in the PDF"))))))
                    .responses(new ApiResponses()
                            .addApiResponse("200", new ApiResponse()
                                    .description("PDF generated successfully")
                                    .content(new Content()
                                            .addMediaType(MediaType.APPLICATION_JSON_VALUE,
                                                    new io.swagger.v3.oas.models.media.MediaType()
                                                            .schema(new ObjectSchema()
                                                                    .addProperty("status", new StringSchema().example("success"))
                                                                    .addProperty("imageCount", new IntegerSchema().example(2))
                                                                    .addProperty("pdfBase64", new StringSchema()
                                                                            .description("Base64-encoded PDF content")
                                                                            .example("JVBERi0xLjQKJdP0z... (truncated)"))))
                                            .addMediaType(MediaType.APPLICATION_PDF_VALUE,
                                                    new io.swagger.v3.oas.models.media.MediaType()
                                                            .schema(new StringSchema().format("binary")))))
                            .addApiResponse("400", createErrorResponse("Invalid input"))
                            .addApiResponse("401", new ApiResponse().description("Unauthorized - Missing or invalid API key"))
                            .addApiResponse("500", createErrorResponse("Internal server error")));

            // Add the operations to the OpenAPI paths
            Paths paths = openApi.getPaths() != null ? openApi.getPaths() : new Paths();
            
            // Note: Removed render-job-ticket-with-images endpoint from documentation
            // as requested
                    
            // Add render-job-ticket-short-work-period endpoint
            Operation shortWorkPeriodOperation = new Operation()
                    .tags(Collections.singletonList("PDF Generation"))
                    .summary("Generate PDF from job ticket with short work period details")
                    .description("Generates a PDF document from a job ticket JSON and optional short work period details. " +
                            "Supports both JSON response with base64-encoded PDF or direct PDF download.")
                    .addParametersItem(new io.swagger.v3.oas.models.parameters.Parameter()
                            .name("Accept")
                            .in("header")
                            .schema(new StringSchema()
                                    .addEnumItem(MediaType.APPLICATION_JSON_VALUE)
                                    .addEnumItem(MediaType.APPLICATION_PDF_VALUE))
                            .description("Response type (defaults to application/json)"))
                    .requestBody(new RequestBody()
                            .content(new Content()
                                    .addMediaType(MediaType.MULTIPART_FORM_DATA_VALUE,
                                            new io.swagger.v3.oas.models.media.MediaType()
                                                    .schema(new ObjectSchema()
                                                            .addProperty("jobTicket", new StringSchema()
                                                                    .description("JSON string containing job ticket data")
                                                                    .example("{\"checklistId\": \"12345\", \"title\": \"Job Ticket\"}"))
                                                            .addProperty("shortWorkPeriod", new StringSchema()
                                                                    .description("Optional JSON string containing short work period details")
                                                                    .example("{\"workOrderNumber\": \"WO-12345\", \"description\": \"Maintenance work\"}"))))))
                    .responses(new ApiResponses()
                            .addApiResponse("200", new ApiResponse()
                                    .description("PDF generated successfully")
                                    .content(new Content()
                                            .addMediaType(MediaType.APPLICATION_JSON_VALUE,
                                                    new io.swagger.v3.oas.models.media.MediaType()
                                                            .schema(new ObjectSchema()
                                                                    .addProperty("status", new StringSchema().example("success"))
                                                                    .addProperty("filename", new StringSchema().example("job-ticket-with-work-order.pdf"))
                                                                    .addProperty("pdfBase64", new StringSchema()
                                                                            .description("Base64-encoded PDF content")
                                                                            .example("JVBERi0xLjQKJdP0z... (truncated)"))))
                                            .addMediaType(MediaType.APPLICATION_PDF_VALUE,
                                                    new io.swagger.v3.oas.models.media.MediaType()
                                                            .schema(new StringSchema().format("binary")))))
                            .addApiResponse("400", createErrorResponse("Invalid input"))
                            .addApiResponse("401", new ApiResponse().description("Unauthorized - Missing or invalid API key"))
                            .addApiResponse("500", createErrorResponse("Internal server error")));
                    
            paths.addPathItem("/api/pdf/render-job-ticket-short-work-period",
                    new PathItem().post(shortWorkPeriodOperation));
                    
            openApi.setPaths(paths);
        };
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