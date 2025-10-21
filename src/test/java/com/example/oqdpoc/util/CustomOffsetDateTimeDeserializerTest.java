package com.example.oqdpoc.util;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomOffsetDateTimeDeserializerTest {

    private CustomOffsetDateTimeDeserializer deserializer;
    
    @Mock
    private JsonParser jsonParser;
    
    @Mock
    private DeserializationContext context;
    
    @BeforeEach
    void setUp() {
        deserializer = new CustomOffsetDateTimeDeserializer();
    }

    @Test
    void deserialize_shouldHandleIsoOffsetDateTime() throws IOException {
        // Arrange
        String dateStr = "2023-10-21T12:34:56.789+02:00";
        when(jsonParser.getText()).thenReturn(dateStr);
        
        // Act
        OffsetDateTime result = deserializer.deserialize(jsonParser, context);
        
        // Assert
        assertNotNull(result);
        assertEquals(2023, result.getYear());
        assertEquals(10, result.getMonthValue());
        assertEquals(21, result.getDayOfMonth());
        assertEquals(12, result.getHour());
        assertEquals(34, result.getMinute());
        assertEquals(56, result.getSecond());
        assertEquals(789000000, result.getNano());
        assertEquals(ZoneOffset.ofHours(2), result.getOffset());
    }

    @Test
    void deserialize_shouldHandleIsoUtc() throws IOException {
        // Arrange
        String dateStr = "2023-10-21T10:34:56.789Z";
        when(jsonParser.getText()).thenReturn(dateStr);
        
        // Act
        OffsetDateTime result = deserializer.deserialize(jsonParser, context);
        
        // Assert
        assertNotNull(result);
        assertEquals(ZoneOffset.UTC, result.getOffset());
    }

    @Test
    void deserialize_shouldHandleMicrosecondPrecision() throws IOException {
        // Arrange
        String dateStr = "2023-10-21T10:34:56.123456";
        when(jsonParser.getText()).thenReturn(dateStr);
        
        // Act
        OffsetDateTime result = deserializer.deserialize(jsonParser, context);
        
        // Assert
        assertNotNull(result);
        assertEquals(123456000, result.getNano());
        assertEquals(ZoneOffset.UTC, result.getOffset());
    }

    @Test
    void deserialize_shouldHandleMillisecondPrecision() throws IOException {
        // Arrange
        String dateStr = "2023-10-21T10:34:56.123";
        when(jsonParser.getText()).thenReturn(dateStr);
        
        // Act
        OffsetDateTime result = deserializer.deserialize(jsonParser, context);
        
        // Assert
        assertNotNull(result);
        assertEquals(123000000, result.getNano());
    }

    @Test
    void deserialize_shouldHandleNoFractionalSeconds() throws IOException {
        // Arrange
        String dateStr = "2023-10-21T10:34:56";
        when(jsonParser.getText()).thenReturn(dateStr);
        
        // Act
        OffsetDateTime result = deserializer.deserialize(jsonParser, context);
        
        // Assert
        assertNotNull(result);
        assertEquals(0, result.getNano());
        assertEquals(56, result.getSecond());
    }

    @Test
    void deserialize_shouldThrowOnInvalidFormat() throws IOException {
        // Arrange
        String invalidDateStr = "not-a-valid-date";
        when(jsonParser.getText()).thenReturn(invalidDateStr);
        
        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            deserializer.deserialize(jsonParser, context);
        });
        
        assertTrue(exception.getMessage().contains("Invalid date format"));
    }

    @Test
    void deserialize_shouldHandleTrimmedInput() throws IOException {
        // Arrange
        String dateStr = "  2023-10-21T10:34:56.789Z  ";
        when(jsonParser.getText()).thenReturn(dateStr);
        
        // Act
        OffsetDateTime result = deserializer.deserialize(jsonParser, context);
        
        // Assert
        assertNotNull(result);
        assertEquals(2023, result.getYear());
        assertEquals(10, result.getMonthValue());
    }

    @Test
    void deserialize_shouldHandleDifferentTimezones() throws IOException {
        // Arrange
        String dateStr = "2023-10-21T15:34:56.789-05:00";
        when(jsonParser.getText()).thenReturn(dateStr);
        
        // Act
        OffsetDateTime result = deserializer.deserialize(jsonParser, context);
        
        // Assert
        assertNotNull(result);
        assertEquals(ZoneOffset.ofHours(-5), result.getOffset());
    }
}
