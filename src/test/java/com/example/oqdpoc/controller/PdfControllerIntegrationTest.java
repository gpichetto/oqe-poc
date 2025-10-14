package com.example.oqdpoc.controller;

import com.example.oqdpoc.exception.PdfGenerationException;
import com.example.oqdpoc.model.jobticket.JobTicket;
import com.example.oqdpoc.service.PdfGenerationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class PdfControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PdfGenerationService pdfGenerationService;

    private final ObjectMapper objectMapper = new ObjectMapper();

//    @Test
    void renderJobTicket_WithValidRequest_ReturnsPdf() throws Exception {
        // Arrange
        JobTicket jobTicket = createValidJobTicket();
        byte[] mockPdf = "%PDF-1.4 mock job ticket pdf".getBytes();
        when(pdfGenerationService.generateJobTicketPdf(any(JobTicket.class))).thenReturn(mockPdf);

        // Act & Assert - Test PDF response
        mockMvc.perform(post("/api/pdf/render-job-ticket")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_PDF)
                        .content(objectMapper.writeValueAsString(jobTicket)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andExpect(header().string("Content-Disposition", "filename=job-ticket.pdf"))
                .andExpect(content().bytes(mockPdf));
    }

//    @Test
    void renderJobTicket_WithValidRequest_ReturnsJsonWithBase64Pdf() throws Exception {
        // Arrange
        JobTicket jobTicket = createValidJobTicket();
        byte[] mockPdf = "%PDF-1.4 mock job ticket pdf".getBytes();
        when(pdfGenerationService.generateJobTicketPdf(any(JobTicket.class))).thenReturn(mockPdf);

        // Act & Assert - Test JSON response with base64 PDF
        mockMvc.perform(post("/api/pdf/render-job-ticket")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(jobTicket)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.filename").value("job-ticket.pdf"))
                .andExpect(jsonPath("$.pdfBase64").isString())
                .andExpect(jsonPath("$.checklistId").value("test-checklist-123"));
    }

//    @Test
    void renderJobTicket_WithNullJobTicket_ReturnsBadRequest() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/pdf/render-job-ticket")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Job ticket data is required"));
    }

//    @Test
    void renderJobTicket_WithMissingRequiredFields_ReturnsBadRequest() throws Exception {
        // Arrange - Create job ticket without required fields
        JobTicket jobTicket = new JobTicket();
        // Missing id, checklistId, and userId

        // Act & Assert
        mockMvc.perform(post("/api/pdf/render-job-ticket")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(jobTicket)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"));
    }

//    @Test
    void renderJobTicket_WhenPdfGenerationFails_ReturnsInternalServerError() throws Exception {
        // Arrange
        JobTicket jobTicket = createValidJobTicket();
        when(pdfGenerationService.generateJobTicketPdf(any(JobTicket.class)))
                .thenThrow(new PdfGenerationException("Failed to generate PDF"));

        // Act & Assert
        mockMvc.perform(post("/api/pdf/render-job-ticket")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(jobTicket)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("PDF Generation Failed"))
                .andExpect(jsonPath("$.message").value("Failed to generate Job Ticket PDF: Failed to generate PDF"));
    }

//    @Test
    void renderJobTicket_WhenUnexpectedErrorOccurs_ReturnsInternalServerError() throws Exception {
        // Arrange
        JobTicket jobTicket = createValidJobTicket();
        when(pdfGenerationService.generateJobTicketPdf(any(JobTicket.class)))
                .thenThrow(new RuntimeException("Unexpected error"));

        // Act & Assert
        mockMvc.perform(post("/api/pdf/render-job-ticket")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(jobTicket)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Internal Server Error"))
                .andExpect(jsonPath("$.message").value("An unexpected error occurred: Unexpected error"));
    }

    private JobTicket createValidJobTicket() {
        JobTicket jobTicket = new JobTicket();
        jobTicket.setId("test-id-123");
        jobTicket.setChecklistId("test-checklist-123");
        jobTicket.setUserId("test-user-456");
        return jobTicket;
    }
}
