package com.example.oqdpoc.service;

import com.example.oqdpoc.exception.PdfGenerationException;
import com.example.oqdpoc.model.jobticket.JobTicket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PdfGenerationServiceTest {

    @Mock
    private TemplateEngine templateEngine;

    @InjectMocks
    private PdfGenerationService pdfGenerationService;

    private JobTicket testJobTicket;

    @BeforeEach
    void setUp() {
        testJobTicket = new JobTicket();
        testJobTicket.setId("test-id");
        testJobTicket.setChecklistId("checklist-1");
    }


    @Test
    void generateJobTicketPdf_WithValidJobTicket_ReturnsPdfBytes() {
        // Arrange
        String mockHtml = "<html>Job Ticket HTML</html>";
        when(templateEngine.process(eq("jobTicket"), any(Context.class))).thenReturn(mockHtml);

        // Act
        byte[] result = pdfGenerationService.generateJobTicketPdf(testJobTicket);

        // Assert
        assertNotNull(result, "Generated PDF should not be null");
        assertTrue(result.length > 0, "Generated PDF should not be empty");
        
        // Verify template engine was called with the correct template name and context
        ArgumentCaptor<Context> contextCaptor = ArgumentCaptor.forClass(Context.class);
        verify(templateEngine).process(eq("jobTicket"), contextCaptor.capture());
        
        // Verify context contains the job ticket and logo URL
        Context capturedContext = contextCaptor.getValue();
        assertNotNull(capturedContext.getVariable("jobTicket"), "Context should contain jobTicket");
        assertNotNull(capturedContext.getVariable("logoUrl"), "Context should contain logoUrl");
        assertTrue(((String) capturedContext.getVariable("logoUrl")).startsWith("file:"), 
            "Logo URL should start with 'file:'");
    }

    @Test
    void generateJobTicketPdf_WithNullJobTicket_ThrowsException() {
        // Act & Assert
        NullPointerException exception = assertThrows(
            NullPointerException.class, 
            () -> pdfGenerationService.generateJobTicketPdf(null)
        );
        
        assertEquals("Job ticket cannot be null", exception.getMessage(), 
            "Exception message should indicate job ticket cannot be null");
    }

    @Test
    void generateJobTicketPdf_WhenTemplateProcessingFails_ThrowsPdfGenerationException() {
        // Arrange
        String errorMessage = "Template processing failed";
        when(templateEngine.process(eq("jobTicket"), any(Context.class)))
            .thenThrow(new RuntimeException(errorMessage));

        // Act & Assert
        PdfGenerationException exception = assertThrows(
            PdfGenerationException.class, 
            () -> pdfGenerationService.generateJobTicketPdf(testJobTicket)
        );
        
        assertTrue(exception.getMessage().contains(errorMessage), 
            "Exception should contain the original error message");
    }
    
    @Test
    void generateJobTicketPdf_WithMinimalJobTicket_ReturnsPdf() {
        // Arrange
        JobTicket minimalJobTicket = new JobTicket();
        minimalJobTicket.setId("minimal-id");
        minimalJobTicket.setChecklistId("minimal-checklist");
        
        String mockHtml = "<html>Minimal Job Ticket</html>";
        when(templateEngine.process(eq("jobTicket"), any(Context.class))).thenReturn(mockHtml);

        // Act
        byte[] result = pdfGenerationService.generateJobTicketPdf(minimalJobTicket);

        // Assert
        assertNotNull(result, "Should generate PDF even with minimal job ticket");
        assertTrue(result.length > 0, "Generated PDF should not be empty");
    }
}
