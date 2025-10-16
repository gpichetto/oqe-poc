package com.example.oqdpoc.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.http.HttpHeaders;
import com.example.oqdpoc.config.FileUploadProperties;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.springframework.util.unit.DataSize;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    private final FileUploadProperties fileUploadProperties;
    
    @Autowired
    public GlobalExceptionHandler(FileUploadProperties fileUploadProperties) {
        this.fileUploadProperties = fileUploadProperties;
    }

    @ExceptionHandler(PdfGenerationException.class)
    public ResponseEntity<Map<String, Object>> handlePdfGenerationException(
            PdfGenerationException ex, WebRequest request) {
        
        log.error("PDF Generation Error: {}", ex.getMessage(), ex);
        
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("error", "PDF Generation Failed");
        body.put("message", ex.getMessage());
        
        // Add more details in development
        if (log.isDebugEnabled()) {
            if (ex.getCause() != null) {
                body.put("cause", ex.getCause().getMessage());
            }
            body.put("exception", ex.getClass().getName());
            body.put("stackTrace", Arrays.stream(ex.getStackTrace())
                .map(StackTraceElement::toString)
                .collect(Collectors.toList()));
        }
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        return new ResponseEntity<>(body, headers, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Map<String, Object>> handleMaxSizeException(MaxUploadSizeExceededException ex) {
        log.warn("File size limit exceeded: {}", ex.getMessage());
        
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        body.put("status", HttpStatus.PAYLOAD_TOO_LARGE.value());
        body.put("error", "File Size Exceeded");
        
        // Show both file size limits in the error message
        body.put("message", String.format(
            "File upload failed. Maximum file size: %s or Maximum total request size: %s exceeded",
            formatDataSize(fileUploadProperties.getMaxFileSize()),
            formatDataSize(fileUploadProperties.getMaxRequestSize())
        ));
        
        if (log.isDebugEnabled()) {
            body.put("exception", ex.getClass().getName());
        }
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        return new ResponseEntity<>(body, headers, HttpStatus.PAYLOAD_TOO_LARGE);
    }
    
    private String formatDataSize(DataSize size) {
        if (size.toMegabytes() >= 1) {
            return size.toMegabytes() + "MB";
        } else if (size.toKilobytes() >= 1) {
            return size.toKilobytes() + "KB";
        } else {
            return size.toBytes() + " bytes";
        }
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleAllExceptions(Exception ex, WebRequest request) {
        log.error("Unhandled exception occurred: {}", ex.getMessage(), ex);
        
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("error", "Internal Server Error");
        
        // Handle template parsing errors specifically
        String errorMessage = ex.getMessage();
        if (errorMessage != null && errorMessage.contains("template parsing")) {
            errorMessage = "Error processing template: " + 
                errorMessage.substring(0, Math.min(errorMessage.length(), 200));
        } else {
            errorMessage = ex.getMessage() != null ? ex.getMessage() : "An unexpected error occurred";
        }
        body.put("message", errorMessage);
        
        // Add more details in development
        if (log.isDebugEnabled()) {
            if (ex.getCause() != null) {
                body.put("cause", ex.getCause().getMessage());
            }
            body.put("exception", ex.getClass().getName());
            if (ex.getStackTrace() != null && ex.getStackTrace().length > 0) {
                body.put("stackTrace", Arrays.stream(ex.getStackTrace())
                    .limit(10) // Limit stack trace size
                    .map(StackTraceElement::toString)
                    .collect(Collectors.toList()));
            }
        }
        
        if (request.getUserPrincipal() != null && request.isUserInRole("ADMIN")) {
            body.put("details", ex.getMessage());
        }
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        return new ResponseEntity<>(body, headers, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
