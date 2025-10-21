package com.example.oqdpoc.validator;

import com.example.oqdpoc.exception.FileTypeValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileTypeValidatorTest {

    private FileTypeValidator fileTypeValidator;

    @Mock
    private MultipartFile mockMultipartFile;

    @BeforeEach
    void setUp() {
        fileTypeValidator = new FileTypeValidator();
    }

    @Test
    void validateImageFiles_shouldAcceptValidJpegFile() throws IOException {
        // Arrange
        // Load the test image from resources
        ClassPathResource resource = new ClassPathResource("images/test.jpg");
        byte[] jpegData = Files.readAllBytes(resource.getFile().toPath());
        
        MockMultipartFile file = new MockMultipartFile(
            "test.jpg",
            "test.jpg",
            "image/jpeg",
            jpegData
        );

        // Act & Assert
        assertDoesNotThrow(() -> fileTypeValidator.validateImageFiles(List.of(file)));
    }

    @Test
    void validateImageFiles_shouldAcceptValidPngFile() throws IOException {
        // Minimal 1x1 transparent PNG file
        ClassPathResource resource = new ClassPathResource("images/test.png");
        byte[] pngBytes = Files.readAllBytes(resource.getFile().toPath());

        MockMultipartFile file = new MockMultipartFile(
            "test.png",
            "test.png",
            "image/png",
            pngBytes
        );

        // Act & Assert
        assertDoesNotThrow(() -> fileTypeValidator.validateImageFiles(List.of(file)));
    }

    @Test
    void validateImageFiles_shouldAcceptValidSvgFile() {
        // Arrange
        // Minimal valid SVG content
        String svgContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<svg xmlns=\"http://www.w3.org/2000/svg\" \n" +
                "     width=\"100\" \n" +
                "     height=\"100\">\n" +
                "    <rect width=\"100\" height=\"100\" fill=\"blue\"/>\n" +
                "</svg>";

        MockMultipartFile file = new MockMultipartFile(
            "test.svg",
            "test.svg",
            "image/svg+xml",
            svgContent.getBytes()
        );

        // Act & Assert
        assertDoesNotThrow(() -> fileTypeValidator.validateImageFiles(List.of(file)));
    }

    @Test
    void validateImageFiles_shouldRejectInvalidFileType() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
            "test.txt",
            "test.txt",
            "text/plain",
            "test content".getBytes()
        );

        // Act & Assert
        FileTypeValidationException exception = assertThrows(
            FileTypeValidationException.class,
            () -> fileTypeValidator.validateImageFiles(List.of(file))
        );
        assertTrue(exception.getMessage().contains("Invalid file type"));
    }

    @Test
    void validateImageFiles_shouldHandleNullFileList() {
        // Act & Assert
        assertDoesNotThrow(() -> fileTypeValidator.validateImageFiles(null));
    }

    @Test
    void validateImageFiles_shouldHandleEmptyFileList() {
        // Act & Assert
        assertDoesNotThrow(() -> fileTypeValidator.validateImageFiles(List.of()));
    }

    @Test
    void validateImageFiles_shouldHandleNullFileInList() {
        // Arrange
        List<MultipartFile> files = Arrays.asList(null, null);

        // Act & Assert
        assertDoesNotThrow(() -> fileTypeValidator.validateImageFiles(files));
    }

    @Test
    void validateImageFiles_shouldHandleEmptyFile() {
        // Arrange
        MockMultipartFile emptyFile = new MockMultipartFile(
            "empty.jpg",
            "empty.jpg",
            "image/jpeg",
            new byte[0]
        );

        // Act & Assert
        assertDoesNotThrow(() -> fileTypeValidator.validateImageFiles(List.of(emptyFile)));
    }

    @Test
    void validateImageFiles_shouldRejectInvalidImageContent() {
        // Arrange
        MockMultipartFile invalidImage = new MockMultipartFile(
            "invalid.jpg",
            "invalid.jpg",
            "image/jpeg",
            "not a real image".getBytes()
        );

        // Act & Assert
        FileTypeValidationException exception = assertThrows(
            FileTypeValidationException.class,
            () -> fileTypeValidator.validateImageFiles(List.of(invalidImage))
        );
        assertTrue(exception.getMessage().contains("Invalid image content"));
    }

    @Test
    void validateImageFiles_shouldHandleIOException() throws IOException {
        // Arrange
        when(mockMultipartFile.getContentType()).thenReturn("image/jpeg");
        when(mockMultipartFile.getOriginalFilename()).thenReturn("test.jpg");
        when(mockMultipartFile.getBytes()).thenThrow(new IOException("Test IO Exception"));

        // Act & Assert
        FileTypeValidationException exception = assertThrows(
            FileTypeValidationException.class,
            () -> fileTypeValidator.validateImageFiles(List.of(mockMultipartFile))
        );
        assertTrue(exception.getMessage().contains("Error processing image"));
    }
}
