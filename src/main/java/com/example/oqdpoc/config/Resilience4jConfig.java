package com.example.oqdpoc.config;

import io.github.resilience4j.common.retry.configuration.RetryConfigCustomizer;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.util.function.Predicate;

@Configuration
public class Resilience4jConfig {

    // Retry configuration
    public static final String PDF_GENERATION_RETRY = "pdfGeneration";
    
    // Rate limiter configuration
    public static final String PDF_GENERATION_RATE_LIMITER = "pdfGenerationRateLimiter";
    private static final int RATE_LIMIT_REFRESH_PERIOD_SECONDS = 60; // 1 minute window
    private static final int RATE_LIMIT_FOR_PERIOD = 10; // 10 requests per window
    private static final int RATE_LIMIT_TIMEOUT_MILLIS = 1000; // 1 second timeout
    
    // Circuit breaker configuration
    public static final String PDF_GENERATION_CIRCUIT_BREAKER = "pdfGenerationCircuitBreaker";
    private static final int CIRCUIT_BREAKER_SLIDING_WINDOW_SIZE = 10;
    private static final float CIRCUIT_BREAKER_FAILURE_RATE_THRESHOLD = 50.0f;
    private static final int CIRCUIT_BREAKER_WAIT_DURATION_SECONDS = 30;
    
    // Common predicate for retry and circuit breaker
    private final Predicate<Throwable> retryAndCircuitBreakerPredicate = throwable -> {
        if (throwable instanceof ResponseStatusException) {
            return ((ResponseStatusException) throwable).getStatusCode().is5xxServerError();
        }
        return !(throwable instanceof IllegalArgumentException);
    };

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
            .retryOnException(retryAndCircuitBreakerPredicate)
            .build();
            
        return RetryRegistry.of(config);
    }
    
    /**
     * Configures rate limiting for PDF generation endpoints
     */
    @Bean
    public RateLimiterRegistry rateLimiterRegistry() {
        RateLimiterConfig config = RateLimiterConfig.custom()
            .limitRefreshPeriod(Duration.ofSeconds(RATE_LIMIT_REFRESH_PERIOD_SECONDS))
            .limitForPeriod(RATE_LIMIT_FOR_PERIOD)
            .timeoutDuration(Duration.ofMillis(RATE_LIMIT_TIMEOUT_MILLIS))
            .build();
            
        return RateLimiterRegistry.of(config);
    }
    
    /**
     * Creates a rate limiter instance for PDF generation
     */
    @Bean
    public RateLimiter pdfGenerationRateLimiter(RateLimiterRegistry rateLimiterRegistry) {
        return rateLimiterRegistry.rateLimiter(PDF_GENERATION_RATE_LIMITER);
    }
    
    /**
     * Configures circuit breaker for PDF generation
     */
    @Bean
    public CircuitBreakerRegistry circuitBreakerRegistry() {
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
            .slidingWindowSize(CIRCUIT_BREAKER_SLIDING_WINDOW_SIZE)
            .failureRateThreshold(CIRCUIT_BREAKER_FAILURE_RATE_THRESHOLD)
            .waitDurationInOpenState(Duration.ofSeconds(CIRCUIT_BREAKER_WAIT_DURATION_SECONDS))
            .recordExceptions(Exception.class)
            .ignoreExceptions(IllegalArgumentException.class)
            .build();
            
        return CircuitBreakerRegistry.of(config);
    }
    
    /**
     * Creates a circuit breaker instance for PDF generation
     */
    @Bean
    public CircuitBreaker pdfGenerationCircuitBreaker(CircuitBreakerRegistry circuitBreakerRegistry) {
        return circuitBreakerRegistry.circuitBreaker(PDF_GENERATION_CIRCUIT_BREAKER);
    }
}
