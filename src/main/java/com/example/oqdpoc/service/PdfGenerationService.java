package com.example.oqdpoc.service;

import com.example.oqdpoc.exception.PdfGenerationException;
import com.example.oqdpoc.model.ChecklistItem;
import com.example.oqdpoc.model.jobticket.JobTicket;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Objects;

@Service
public class PdfGenerationService {
    
    private static final Logger log = LoggerFactory.getLogger(PdfGenerationService.class);
    private final TemplateEngine templateEngine;

    private static final String CHECKLIST_TEMPLATE = "checklist";
    private static final String JOB_TICKET_TEMPLATE = "jobTicket";
    
    public PdfGenerationService(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }


    /**
     * Generates a PDF document from a list of checklist items
     *
     * @param checklistItems List of items to include in the PDF
     * @return byte array containing the generated PDF
     * @throws PdfGenerationException if there's an error generating the PDF
     */
    public byte[] generatePdf(List<ChecklistItem> checklistItems) {
        Objects.requireNonNull(checklistItems, "Checklist items cannot be null");
        
        log.debug("Starting PDF generation for {} items", checklistItems.size());
        
        try {
            // Process the template with Thymeleaf
            Context context = new Context();
            context.setVariable("items", checklistItems);
            
            String html = templateEngine.process(CHECKLIST_TEMPLATE, context);
            
            // Convert HTML to PDF
            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                PdfRendererBuilder builder = new PdfRendererBuilder()
                    .withHtmlContent(html, "")
                    .toStream(outputStream);
                
                builder.run();
                
                log.debug("Successfully generated PDF with {} bytes", outputStream.size());
                return outputStream.toByteArray();
            }
        } catch (Exception e) {
            log.error("Failed to generate PDF: {}", e.getMessage(), e);
            throw new PdfGenerationException("Failed to generate PDF: " + e.getMessage(), e);
        }
    }
    
    /**
     * Generates a PDF document from a Job Ticket
     *
     * @param jobTicket The job ticket data to render
     * @return byte array containing the generated PDF
     * @throws PdfGenerationException if there's an error generating the PDF
     */
    public byte[] generateJobTicketPdf(JobTicket jobTicket) {
        Objects.requireNonNull(jobTicket, "Job ticket cannot be null");
        
        log.debug("Starting Job Ticket PDF generation for checklist ID: {}", jobTicket.getChecklistId());
        
        try {
            // Create and populate the Thymeleaf context
            Context context = new Context();
            context.setVariable("jobTicket", jobTicket);
            
            // Set the logo URL with full file path
            String logoPath = new java.io.File("src/main/resources/static/images/thales-logo.png").getAbsolutePath();
            context.setVariable("logoUrl", "file:" + logoPath);
            
            // Process the template
            String html = templateEngine.process(JOB_TICKET_TEMPLATE, context);
            
            // Convert HTML to PDF
            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                PdfRendererBuilder builder = new PdfRendererBuilder()
                    .withHtmlContent(html, "")
                    .toStream(outputStream);
                
                builder.run();
                
                log.debug("Successfully generated Job Ticket PDF with {} bytes", outputStream.size());
                return outputStream.toByteArray();
            }
        } catch (Exception e) {
            log.error("Failed to generate Job Ticket PDF: {}", e.getMessage(), e);
            throw new PdfGenerationException("Failed to generate Job Ticket PDF: " + e.getMessage(), e);
        }
    }
}
