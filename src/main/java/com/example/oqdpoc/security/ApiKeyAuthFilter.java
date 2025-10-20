package com.example.oqdpoc.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class ApiKeyAuthFilter extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(ApiKeyAuthFilter.class);
    private final String apiKey;
    private static final String API_KEY_HEADER = "X-API-KEY";

    public ApiKeyAuthFilter(String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                  HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        String requestUri = request.getRequestURI();
        String method = request.getMethod();
        String contentType = request.getContentType();
        
        log.debug("Processing request to: {} {}", method, requestUri);
        log.debug("Content-Type: {}", contentType);
        
        // Skip authentication for preflight requests and health check
        if (isPublicPath(requestUri) || "OPTIONS".equalsIgnoreCase(method)) {
            log.debug("Skipping authentication for public path or OPTIONS request");
            filterChain.doFilter(request, response);
            return;
        }
        
        // Check API key from headers (don't read the request body)
        String requestApiKey = request.getHeader(API_KEY_HEADER);
        
        if (requestApiKey == null || requestApiKey.trim().isEmpty()) {
            log.warn("API key is missing in request");
            sendErrorResponse(response, "API key is missing");
            return;
        }
        
        if (!apiKey.equals(requestApiKey)) {
            log.warn("Invalid API key provided");
            sendErrorResponse(response, "Invalid API key");
            return;
        }
        
        try {
            Authentication auth = new ApiKeyAuthentication(apiKey);
            SecurityContextHolder.getContext().setAuthentication(auth);
            
            log.debug("Authentication successful, proceeding with filter chain");
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            log.error("Error processing API key authentication", e);
            sendErrorResponse(response, "Error processing authentication: " + e.getMessage());
        }
    }
    
    private boolean isPublicPath(String requestUri) {
        return requestUri != null && (
            "/actuator/health".equals(requestUri) ||
            requestUri.startsWith("/v3/api-docs") ||
            requestUri.startsWith("/swagger-ui") ||
            requestUri.startsWith("/swagger-resources") ||
            requestUri.startsWith("/webjars") ||
            "/swagger-ui.html".equals(requestUri)
        );
    }
    
    private void sendErrorResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(
            String.format("{\"status\": 401, \"error\": \"Unauthorized\", \"message\": \"%s\"}", 
            message.replace("\"", "'"))
        );
    }
}
