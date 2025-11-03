package com.example.oqdpoc.service;

import com.example.oqdpoc.exception.PdfGenerationException;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.ByteArrayOutputStream;

@Service
@CacheConfig(cacheNames = "jobTicketTemplates")
public class PdfGenerationService {

    private static final Logger log = LoggerFactory.getLogger(PdfGenerationService.class);


    /**
     * Generates a PDF document from an HTML string using the V2 template with retry capability
     *
     * @param html The HTML content to convert to PDF
     * @return byte array containing the generated PDF
     * @throws PdfGenerationException if there's an error generating the PDF after all retry attempts
     */
    @Retry(name = "pdfGeneration", fallbackMethod = "generatePdfWithWorkOrderFallback")
    public byte[] generatePdfWithWorkOrder(String html) {
        log.debug("Generating PDF with work order from HTML content");
        if (!StringUtils.hasText(html)) {
            throw new IllegalArgumentException("The content cannot be null or empty");
        }

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder()
                    .withHtmlContent(html, "")
                    .toStream(outputStream);

            builder.run();
            log.debug("Successfully generated PDF with work order, size: {} bytes", outputStream.size());
            return outputStream.toByteArray();
        } catch (Exception e) {
            log.error("Error generating PDF with work order from HTML: {}", e.getMessage());
            throw new PdfGenerationException("Failed to generate PDF from HTML with work order: " + e.getMessage(), e);
        }
    }

    /**
     * Fallback method for generatePdfWithWorkOrder
     */
    public byte[] generatePdfWithWorkOrderFallback(String html, Exception e) {
        log.error("Fallback triggered for generatePdfWithWorkOrder after retries: {}", e.getMessage());
        throw new PdfGenerationException("PDF generation with work order failed after retries: " + e.getMessage(), e);
    }
}