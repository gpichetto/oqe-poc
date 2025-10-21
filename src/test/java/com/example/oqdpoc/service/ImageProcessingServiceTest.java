package com.example.oqdpoc.service;

import com.example.oqdpoc.exception.FileTypeValidationException;
import com.example.oqdpoc.validator.FileTypeValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImageProcessingServiceTest {

    @Mock
    private FileTypeValidator fileTypeValidator;

    @InjectMocks
    private ImageProcessingService imageProcessingService;

    private MockMultipartFile validImage;
    private MockMultipartFile invalidImage;

    @BeforeEach
    void setUp() {
        // Create a valid test image (minimal valid JPEG)
        validImage = new MockMultipartFile(
            "test.jpg",
            "test.jpg",
            "image/jpeg",
            new byte[]{(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0, 0x00, 0x10, 'J', 'F', 'I', 'F', 0x00, 0x01, 0x01, 0x00, 0x00, 0x01, 0x00, 0x01, 0x00, 0x00, (byte) 0xFF, (byte) 0xDB, 0x00, 0x43, 0x00, 0x08, 0x06, 0x06, 0x07, 0x06, 0x05, 0x08, 0x07, 0x07, 0x07, 0x09, 0x09, 0x08, 0x0A, 0x0C, 0x14, 0x0D, 0x0C, 0x0B, 0x0B, 0x0C, 0x19, 0x12, 0x13, 0x0F, 0x14, 0x1D, 0x1A, 0x1F, 0x1E, 0x1D, 0x1A, 0x1C, 0x1C, 0x20, 0x24, 0x2E, 0x27, 0x20, 0x22, 0x2C, 0x23, 0x1C, 0x1C, 0x29, 0x37, 0x29, 0x2C, 0x30, 0x31, 0x34, 0x34, 0x34, 0x1F, 0x27, 0x39, 0x3D, 0x38, 0x32, 0x3C, 0x2E, 0x33, 0x34, 0x32, (byte) 0xFF, (byte) 0xC0, 0x00, 0x0B, 0x08, 0x00, 0x01, 0x00, 0x01, 0x01, 0x01, 0x11, 0x00, (byte) 0xFF, (byte) 0xC4, 0x00, 0x14, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, (byte) 0xFF, (byte) 0xDA, 0x00, 0x08, 0x01, 0x01, 0x00, 0x00, 0x3F, 0x00, 0x3F, (byte) 0xFF, (byte) 0xD9}
        );

        // Create an invalid test image (empty file)
        invalidImage = new MockMultipartFile(
            "invalid.jpg",
            "invalid.jpg",
            "image/jpeg",
            new byte[0]
        );
    }

    @Test
    void processImages_shouldReturnEmptyListForNullInput() throws FileTypeValidationException {
        // Act
        List<String> result = imageProcessingService.processImages(null);
        
        // Assert
        assertTrue(result.isEmpty());
        verify(fileTypeValidator, never()).validateImageFiles(anyList());
    }

    @Test
    void processImages_shouldReturnEmptyListForEmptyList() throws FileTypeValidationException {
        // Act
        List<String> result = imageProcessingService.processImages(Collections.emptyList());
        
        // Assert
        assertTrue(result.isEmpty());
        verify(fileTypeValidator, never()).validateImageFiles(anyList());
    }

    @Test
    void processImages_shouldProcessValidImages() throws FileTypeValidationException, IOException {
        // Arrange
        List<MultipartFile> files = List.of(validImage);
        
        // Act
        List<String> result = imageProcessingService.processImages(files);
        
        // Assert
        assertEquals(1, result.size());
        assertTrue(result.get(0).startsWith("data:image/jpeg;base64,/9"));
        verify(fileTypeValidator).validateImageFiles(anyList());
    }

    @Test
    void processImages_shouldFilterOutNullFiles() throws FileTypeValidationException {
        // Arrange
        List<MultipartFile> files = Arrays.asList(validImage, null);
        
        // Act
        List<String> result = imageProcessingService.processImages(files);
        
        // Assert
        assertEquals(1, result.size());
        verify(fileTypeValidator).validateImageFiles(anyList());
    }

    @Test
    void processImages_shouldFilterOutEmptyFiles() throws FileTypeValidationException {
        // Arrange
        List<MultipartFile> files = Arrays.asList(validImage, invalidImage);
        
        // Act
        List<String> result = imageProcessingService.processImages(files);
        
        // Assert
        assertEquals(1, result.size());
        verify(fileTypeValidator).validateImageFiles(anyList());
    }

    @Test
    void processImages_shouldRethrowValidationException() throws FileTypeValidationException {
        // Arrange
        List<MultipartFile> files = List.of(validImage);
        doThrow(new FileTypeValidationException("Invalid file type"))
            .when(fileTypeValidator).validateImageFiles(anyList());
        
        // Act & Assert
        assertThrows(FileTypeValidationException.class, 
            () -> imageProcessingService.processImages(files));
        
        verify(fileTypeValidator).validateImageFiles(anyList());
    }

    @Test
    void processImages_shouldHandleMultipleValidImages() throws FileTypeValidationException {
        // Arrange
        List<MultipartFile> files = Arrays.asList(validImage, validImage, validImage);
        
        // Act
        List<String> result = imageProcessingService.processImages(files);
        
        // Assert
        assertEquals(3, result.size());
        result.forEach(img -> assertTrue(img.startsWith("data:image/jpeg;base64,")));
        verify(fileTypeValidator).validateImageFiles(anyList());
    }
}
