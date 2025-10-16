package com.example.oqdpoc.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class ErrorHandlingConfig {

    @Bean
    public ErrorViewResolver customErrorViewResolver() {
        return (HttpServletRequest request, HttpStatus status, Map<String, Object> model) -> {
            Map<String, Object> errorAttributes = new LinkedHashMap<>();
            errorAttributes.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            errorAttributes.put("status", status.value());
            errorAttributes.put("error", status.getReasonPhrase());
            errorAttributes.put("message", model.getOrDefault("message", "No message available"));
            
            // Copy all error attributes to the model
            model.putAll(errorAttributes);
            
            // Return null to let the default error handling take over
            return null;
        };
    }

    @Bean
    public SimpleModule customSerializationModule() {
        SimpleModule module = new SimpleModule();
        module.addSerializer(LocalDateTime.class, new JsonSerializer<LocalDateTime>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

            @Override
            public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                gen.writeString(value.format(formatter));
            }
        });
        return module;
    }
}
