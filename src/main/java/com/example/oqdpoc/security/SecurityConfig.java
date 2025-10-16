package com.example.oqdpoc.security;

import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import java.nio.charset.StandardCharsets;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);

    @Value("${app.api.key}")
    private String apiKey;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        ApiKeyAuthFilter filter = new ApiKeyAuthFilter(apiKey);
        
        http
            .csrf(AbstractHttpConfigurer::disable)  // Disable CSRF for stateless API
            .cors(cors -> cors.configurationSource(request -> {
                var corsConfig = new CorsConfiguration();
                corsConfig.setAllowedOrigins(List.of("*"));
                corsConfig.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                corsConfig.setAllowedHeaders(List.of("*"));
                corsConfig.setExposedHeaders(List.of("X-API-KEY"));
                return corsConfig;
            }))
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/health").permitAll()
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // Allow CORS preflight
                .requestMatchers("/api/pdf/**").permitAll() // Allow PDF endpoints without authentication
                .anyRequest().authenticated()
            )
            // Add the API key filter but exclude PDF endpoints
            .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class)
            .securityMatcher(
                request -> !request.getRequestURI().startsWith("/api/pdf/")
            )
            // Handle authentication exceptions
            .exceptionHandling(exception -> {
                exception.authenticationEntryPoint((request, response, authException) -> {
                    log.error("Authentication error: {}", authException.getMessage());
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setCharacterEncoding(StandardCharsets.UTF_8.name());
                    response.getWriter().write(
                        String.format(
                            "{\"status\": 401, \"error\": \"Unauthorized\", \"message\": \"%s\"}",
                            authException.getMessage() != null ? 
                                authException.getMessage().replace("\"", "'") : 
                                "Authentication failed"
                        )
                    );
                });
                
                exception.accessDeniedHandler((request, response, accessDeniedException) -> {
                    log.error("Access denied: {}", accessDeniedException.getMessage());
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.setCharacterEncoding(StandardCharsets.UTF_8.name());
                    response.getWriter().write(
                        "{\"status\": 403, \"error\": \"Forbidden\", \"message\": \"Access denied\"}"
                    );
                });
            });
            
        return http.build();
    }
}
