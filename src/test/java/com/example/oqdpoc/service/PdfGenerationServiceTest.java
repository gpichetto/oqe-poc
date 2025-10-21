package com.example.oqdpoc.service;

import com.example.oqdpoc.exception.PdfGenerationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PdfGenerationServiceTest {

    @InjectMocks
    private PdfGenerationService pdfGenerationService;

    @Test
    void generateJobTicketPdfWithImages_shouldGeneratePdfSuccessfully() {
        // Arrange
        String testHtml = "<html><body>Test PDF with Images</body></html>";
        
        // Act
        byte[] result = pdfGenerationService.generateJobTicketPdfWithImages(testHtml);
        
        // Assert
        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    @Test
    void generateJobTicketPdfWithImages_shouldThrowWhenHtmlIsNull() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class,
            () -> pdfGenerationService.generateJobTicketPdfWithImages(null));
    }

    @Test
    void generateJobTicketPdfWithImages_shouldThrowWhenHtmlIsEmpty() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, 
            () -> pdfGenerationService.generateJobTicketPdfWithImages(""));
    }
}
