package com.example.oqdpoc.config;

import io.github.resilience4j.common.retry.configuration.RetryConfigCustomizer;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;

@Configuration
public class Resilience4jConfig {

    public static final String PDF_GENERATION_RETRY = "pdfGeneration";

    @Bean
    public RetryConfigCustomizer retryConfigCustomizer() {
        return RetryConfigCustomizer
                .of(PDF_GENERATION_RETRY, builder -> builder
                        .maxAttempts(3)
                        .waitDuration(Duration.ofMillis(500))
                        .retryOnException(throwable -> {
                            // Don't retry on client errors (4xx)
                            if (throwable instanceof ResponseStatusException) {
                                return ((ResponseStatusException) throwable).getStatusCode().is5xxServerError();
                            }
                            // Retry on other exceptions
                            return true;
                        })
                );
    }

    @Bean
    public RetryRegistry retryRegistry() {
        RetryConfig config = RetryConfig.custom()
            .maxAttempts(3)
            .waitDuration(Duration.ofMillis(500))
            .retryOnException(throwable -> {
                // Don't retry on client errors (4xx)
                if (throwable instanceof ResponseStatusException) {
                    return ((ResponseStatusException) throwable).getStatusCode().is5xxServerError();
                }
                // Retry on other exceptions
                return true;
            })
            .build();
            
        return RetryRegistry.of(config);
    }
}
