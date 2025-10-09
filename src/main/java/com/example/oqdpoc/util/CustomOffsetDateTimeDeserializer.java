package com.example.oqdpoc.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public class CustomOffsetDateTimeDeserializer extends JsonDeserializer<OffsetDateTime> {
    
    private static final DateTimeFormatter[] FORMATTERS = {
        DateTimeFormatter.ISO_OFFSET_DATE_TIME,
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
    };

    @Override
    public OffsetDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String dateStr = p.getText().trim();
        
        // If it has 'Z' or timezone offset, parse with timezone
        if (dateStr.endsWith("Z") || dateStr.matches(".*[+-]\\d{2}:?\\d{2}$")) {
            return OffsetDateTime.parse(dateStr, FORMATTERS[0]);
        }
        
        // Otherwise try the other formats without timezone (default to UTC)
        for (int i = 1; i < FORMATTERS.length; i++) {
            try {
                return OffsetDateTime.parse(dateStr, FORMATTERS[i].withZone(java.time.ZoneOffset.UTC));
            } catch (Exception e) {
                // Try next format
            }
        }
        
        throw new IllegalArgumentException("Invalid date format: " + dateStr);
    }
}
