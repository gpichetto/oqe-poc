package com.example.oqdpoc.service;

import com.example.oqdpoc.exception.PdfGenerationException;
import com.example.oqdpoc.model.ChecklistItem;
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
}
