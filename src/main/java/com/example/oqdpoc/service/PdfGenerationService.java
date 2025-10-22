package com.example.oqdpoc.service;

import com.example.oqdpoc.exception.PdfGenerationException;
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

@Service
@CacheConfig(cacheNames = "jobTicketTemplates")
public class PdfGenerationService {

    private static final Logger log = LoggerFactory.getLogger(PdfGenerationService.class);
    private final TemplateEngine templateEngine;

    private static final String JOB_TICKET_TEMPLATE = "jobTicket";
    private static final String CACHE_KEY_PREFIX = "template::";

    public PdfGenerationService(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
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
    private String processJobTicketTemplate(JobTicket jobTicket) {
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
    private byte[] generateJobTicketPdfWithImagesFallback(String html, Exception ex) {
        log.error("All retry attempts failed for HTML to PDF generation", ex);
        throw new PdfGenerationException("Failed to generate PDF from HTML after multiple attempts: " + ex.getMessage(), ex);
    }
}