package com.example.oqdpoc.service;

import com.example.oqdpoc.exception.PdfGenerationException;
import com.example.oqdpoc.model.ChecklistItem;
import com.example.oqdpoc.model.jobticket.JobTicket;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Objects;

@Service
@CacheConfig(cacheNames = "jobTicketTemplates")
public class PdfGenerationService {

    private static final Logger log = LoggerFactory.getLogger(PdfGenerationService.class);
    private final TemplateEngine templateEngine;

    private static final String CHECKLIST_TEMPLATE = "checklist";
    private static final String JOB_TICKET_TEMPLATE = "jobTicket";
    private static final String CACHE_KEY_PREFIX = "template::";

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
    @Cacheable(key = "#root.target.CACHE_KEY_PREFIX + #root.target.CHECKLIST_TEMPLATE + '::' + T(java.util.Objects).hash(#checklistItems)")
    public String processChecklistTemplate(List<ChecklistItem> checklistItems) {
        Objects.requireNonNull(checklistItems, "Checklist items cannot be null");

        if (checklistItems.isEmpty()) {
            throw new IllegalArgumentException("Checklist items cannot be empty");
        }

        log.debug("Processing checklist template for {} items", checklistItems.size());
        Context context = new Context();
        context.setVariable("items", checklistItems);
        return templateEngine.process(CHECKLIST_TEMPLATE, context);
    }

    public byte[] generatePdf(List<ChecklistItem> checklistItems) {
        log.debug("Starting PDF generation for {} items", checklistItems.size());
        String html = processChecklistTemplate(checklistItems);
        
        try {

            // Convert HTML to PDF
            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                PdfRendererBuilder builder = new PdfRendererBuilder()
                        .withHtmlContent(html, "")
                        .toStream(outputStream);


                log.debug("Successfully generated PDF with {} bytes", outputStream.size());
                return outputStream.toByteArray();
            }
        } catch (Exception e) {
            log.error("Error generating PDF (attempt will be retried)", e);
            throw new PdfGenerationException("Failed to generate PDF: " + e.getMessage(), e);
        }
    }

    /**
     * Generates a PDF document from an HTML string with retry capability
     *
     * @param html The HTML content to convert to PDF
     * @return byte array containing the generated PDF
     * @throws PdfGenerationException if there's an error generating the PDF after all retry attempts
     */
    @Retry(name = "pdfGeneration", fallbackMethod = "generateJobTicketPdfWithImagesFallback")
    public byte[] generateJobTicketPdfWithImages(String html) {
        log.debug("Generating PDF from HTML content");
        if (!StringUtils.hasText(html)) {
            throw new IllegalArgumentException("The content cannot be null or empty");
        }

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder()
                    .withHtmlContent(html, "")
                    .toStream(outputStream);
            
            builder.run();
            log.debug("Successfully generated PDF with {} bytes", outputStream.size());
            return outputStream.toByteArray();
        } catch (Exception e) {
            log.error("Error generating PDF from HTML: {}", e.getMessage());
            throw new PdfGenerationException("Failed to generate PDF from HTML: " + e.getMessage(), e);
        }
    }

    @Cacheable(key = "#root.target.CACHE_KEY_PREFIX + #root.target.JOB_TICKET_TEMPLATE + '::' + #jobTicket.id")
    public String processJobTicketTemplate(JobTicket jobTicket) {
        log.info("Processing job ticket template for ID: {}", jobTicket != null ? jobTicket.getId() : "null");
        
        if (jobTicket == null || jobTicket.getId() == null) {
            throw new IllegalArgumentException("Job ticket and its ID must not be null");
        }
        
        Context context = new Context();
        context.setVariable("jobTicket", jobTicket);
        
        // Set the logo URL with full file path
        String logoPath = new java.io.File("src/main/resources/static/images/thales-logo.png").getAbsolutePath();
        context.setVariable("logoUrl", "file:" + logoPath);
        
        return templateEngine.process(JOB_TICKET_TEMPLATE, context);
    }
    
    /**
     * Generates a PDF for a job ticket with embedded images
     *
     * @param jobTicket The job ticket data
     * @param base64Images List of base64-encoded images to include in the PDF
     * @return byte array containing the generated PDF
     * @throws PdfGenerationException if there's an error generating the PDF
     */
    public byte[] generateJobTicketPdfWithImages(JobTicket jobTicket, List<String> base64Images) {
        log.info("Generating PDF for job ticket with {} images", base64Images != null ? base64Images.size() : 0);
        
        try {
            Context context = new Context();
            context.setVariable("jobTicket", jobTicket);
            context.setVariable("images", base64Images != null ? base64Images : List.of());
            
            String html = templateEngine.process("jobTicketWithImages", context);
            return generateJobTicketPdfWithImages(html);
        } catch (Exception e) {
            log.error("Error generating job ticket PDF with images", e);
            throw new PdfGenerationException("Failed to generate job ticket PDF with images: " + e.getMessage(), e);
        }
    }

    public byte[] generateJobTicketPdf(JobTicket jobTicket) {
        log.info("Generating PDF for job ticket: {}", jobTicket != null ? jobTicket.getId() : "null");
        
        try {
            // Process the template (will use cache if available)
            String html = processJobTicketTemplate(jobTicket);

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
            log.error("Error generating Job Ticket PDF (attempt will be retried)", e);
            throw new PdfGenerationException("Failed to generate Job Ticket PDF: " + e.getMessage(), e);
        }
    }

    /**
     * Fallback method when retries are exhausted for generateJobTicketPdfWithImages
     * @param html The HTML content that failed to be converted to PDF
     * @param ex The exception that caused the failure
     * @return This method always throws an exception
     * @throws PdfGenerationException with details about the failure after all retry attempts
     */
    @SuppressWarnings("unused")
    public byte[] generateJobTicketPdfWithImagesFallback(String html, Exception ex) {
        log.error("All retry attempts failed for HTML to PDF generation", ex);
        throw new PdfGenerationException("Failed to generate PDF from HTML after multiple attempts: " + ex.getMessage(), ex);
    }
}