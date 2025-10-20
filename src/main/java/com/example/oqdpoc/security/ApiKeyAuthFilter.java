package com.example.oqdpoc.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StreamUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
        log.debug("Processing request to: {}", requestUri);
        
        // Skip authentication for preflight requests
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            log.debug("Skipping authentication for preflight request: {}", requestUri);
            filterChain.doFilter(request, response);
            return;
        }

        // Wrap the request to allow multiple reads of the body
        CachedBodyHttpServletRequest cachedBodyHttpServletRequest = 
            new CachedBodyHttpServletRequest(request);
            
        String requestApiKey = cachedBodyHttpServletRequest.getHeader(API_KEY_HEADER);
        
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
            
            // Use the wrapped request that allows multiple reads
            log.debug("Authentication successful, proceeding with filter chain");
            filterChain.doFilter(cachedBodyHttpServletRequest, response);
        } catch (Exception e) {
            log.error("Error processing API key authentication", e);
            sendErrorResponse(response, "Error processing authentication: " + e.getMessage());
        }
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
    
    private static class CachedBodyHttpServletRequest extends HttpServletRequestWrapper {
        private byte[] cachedBody;

        public CachedBodyHttpServletRequest(HttpServletRequest request) throws IOException {
            super(request);
            InputStream requestInputStream = request.getInputStream();
            this.cachedBody = StreamUtils.copyToByteArray(requestInputStream);
        }

        @Override
        public ServletInputStream getInputStream() throws IOException {
            return new CachedBodyServletInputStream(this.cachedBody);
        }

        @Override
        public BufferedReader getReader() throws IOException {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(this.cachedBody);
            return new BufferedReader(new InputStreamReader(byteArrayInputStream));
        }
    }

    private static class CachedBodyServletInputStream extends ServletInputStream {
        private final ByteArrayInputStream inputStream;

        public CachedBodyServletInputStream(byte[] cachedBody) {
            this.inputStream = new ByteArrayInputStream(cachedBody);
        }

        @Override
        public int read() throws IOException {
            return inputStream.read();
        }

        @Override
        public boolean isFinished() {
            return inputStream.available() == 0;
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(ReadListener readListener) {
            throw new UnsupportedOperationException();
        }
    }
}
