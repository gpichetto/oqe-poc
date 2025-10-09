package com.example.oqdpoc.controller;

import com.example.oqdpoc.exception.PdfGenerationException;
import com.example.oqdpoc.model.ChecklistItem;
import com.example.oqdpoc.model.jobticket.JobTicket;
import com.example.oqdpoc.service.PdfGenerationService;
import com.example.oqdpoc.validator.ChecklistItemValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import static org.springframework.http.MediaType.APPLICATION_PDF_VALUE;
import org.springframework.http.ResponseEntity;
import org.thymeleaf.context.Context;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Validated
@RestController
@RequestMapping("/api/pdf")
public class PdfController {
    
    private static final Logger log = LoggerFactory.getLogger(PdfController.class);
    private static final String PDF_FILENAME = "checklist.pdf";
    private static final String JOB_TICKET_PDF_FILENAME = "job-ticket.pdf";

    private final PdfGenerationService pdfGenerationService;
    private final ChecklistItemValidator checklistItemValidator;
    
    public PdfController(PdfGenerationService pdfGenerationService, 
                        ChecklistItemValidator checklistItemValidator) {
        this.pdfGenerationService = pdfGenerationService;
        this.checklistItemValidator = checklistItemValidator;
    }


    /**
     * Creates a standardized error response for validation errors
     */
    private ResponseEntity<Map<String, Object>> createValidationErrorResponse(BindingResult bindingResult) {
        log.warn("Validation errors found: {}", bindingResult.getAllErrors());
        
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Validation Error");
        
        // Collect all field errors
        List<String> errors = bindingResult.getFieldErrors().stream()
            .map(error -> String.format("%s: %s", error.getField(), error.getDefaultMessage()))
            .collect(Collectors.toList());
            
        // Add global errors
        bindingResult.getGlobalErrors().stream()
            .map(error -> String.format("%s: %s", 
                error.getObjectName(), 
                error.getDefaultMessage()))
            .forEach(errors::add);
            
        response.put("message", "Validation failed");
        response.put("errors", errors);
        
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .contentType(MediaType.APPLICATION_JSON)
            .body(response);
    }
    
    @PostMapping(value = "/render", 
        produces = {MediaType.APPLICATION_PDF_VALUE, MediaType.APPLICATION_JSON_VALUE},
        consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> renderPdf(
            @RequestBody List<ChecklistItem> checklistItems,
            @RequestHeader(value = "Accept", required = false) String acceptHeader) {
        
        // Validate each item in the list
        if (checklistItems != null) {
            for (int i = 0; i < checklistItems.size(); i++) {
                ChecklistItem item = checklistItems.get(i);
                DataBinder binder = new DataBinder(item, "checklistItems[" + i + "]");
                BindingResult bindingResult = binder.getBindingResult();
                
                // Perform validation
                checklistItemValidator.validate(item, bindingResult);
                
                if (bindingResult.hasErrors()) {
                    return createValidationErrorResponse(bindingResult);
                }
            }
        }
        
        log.info("Received request to generate PDF with {} items", 
            checklistItems != null ? checklistItems.size() : 0);
        
        if (checklistItems == null || checklistItems.isEmpty()) {
            log.warn("Empty or null checklist items provided");
            return ResponseEntity.badRequest().body(
                Map.of(
                    "timestamp", LocalDateTime.now(),
                    "status", HttpStatus.BAD_REQUEST.value(),
                    "error", "Bad Request",
                    "message", "At least one checklist item is required"
                )
            );
        }
        
        try {
            byte[] pdfBytes = pdfGenerationService.generatePdf(checklistItems);
            
            // Check the Accept header to determine the response type
            boolean preferJson = acceptHeader != null && acceptHeader.contains(MediaType.APPLICATION_JSON_VALUE);
            
            if (preferJson) {
                // Return JSON response with base64-encoded PDF
                String base64Pdf = Base64.getEncoder().encodeToString(pdfBytes);
                return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of(
                        "status", "success",
                        "filename", PDF_FILENAME,
                        "pdfBase64", base64Pdf
                    ));
            } else {
                // Return raw PDF file
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_PDF);
                headers.setContentDispositionFormData("filename", PDF_FILENAME);
                headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
                return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
            }
            
        } catch (PdfGenerationException e) {
            log.error("Error generating PDF: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of(
                    "timestamp", LocalDateTime.now(),
                    "status", HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "error", "PDF Generation Failed",
                    "message", "Failed to generate PDF: " + e.getMessage()
                ));
        } catch (Exception e) {
            log.error("Unexpected error generating PDF", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of(
                    "timestamp", LocalDateTime.now(),
                    "status", HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "error", "Internal Server Error",
                    "message", "An unexpected error occurred: " + e.getMessage()
                ));
        }
    }
    
    /**
     * Renders a Job Ticket JSON into a PDF document
     * 
     * @param jobTicket The job ticket data to render
     * @param acceptHeader Optional Accept header to determine response format
     * @return ResponseEntity containing either the PDF bytes or a JSON response with base64-encoded PDF
     */
    @PostMapping(value = "/render-job-ticket", 
        produces = {APPLICATION_PDF_VALUE, MediaType.APPLICATION_JSON_VALUE},
        consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> renderJobTicket(
            @RequestBody JobTicket jobTicket,
            @RequestHeader(value = "Accept", required = false) String acceptHeader) {
        
        log.info("Received request to generate Job Ticket PDF for checklist ID: {}", 
            jobTicket != null ? jobTicket.getChecklistId() : "null");
        
        if (jobTicket == null) {
            log.warn("Null job ticket provided");
            return ResponseEntity.badRequest().body(
                Map.of(
                    "timestamp", LocalDateTime.now(),
                    "status", HttpStatus.BAD_REQUEST.value(),
                    "error", "Bad Request",
                    "message", "Job ticket data is required"
                )
            );
        }
        
        try {
            // Generate the PDF - let the service handle all template processing
            byte[] pdfBytes = pdfGenerationService.generateJobTicketPdf(jobTicket);
            
            // Check the Accept header to determine the response type
            boolean preferJson = acceptHeader != null && acceptHeader.contains(MediaType.APPLICATION_JSON_VALUE);
            
            if (preferJson) {
                // Return JSON response with base64-encoded PDF
                String base64Pdf = Base64.getEncoder().encodeToString(pdfBytes);
                return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of(
                        "status", "success",
                        "filename", JOB_TICKET_PDF_FILENAME,
                        "pdfBase64", base64Pdf,
                        "checklistId", jobTicket.getChecklistId()
                    ));
            } else {
                // Return raw PDF file
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_PDF);
                headers.setContentDispositionFormData("filename", JOB_TICKET_PDF_FILENAME);
                headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
                return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
            }
            
        } catch (PdfGenerationException e) {
            log.error("Error generating Job Ticket PDF: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of(
                    "timestamp", LocalDateTime.now(),
                    "status", HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "error", "PDF Generation Failed",
                    "message", "Failed to generate Job Ticket PDF: " + e.getMessage()
                ));
        } catch (Exception e) {
            log.error("Unexpected error generating Job Ticket PDF", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of(
                    "timestamp", LocalDateTime.now(),
                    "status", HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "error", "Internal Server Error",
                    "message", "An unexpected error occurred: " + e.getMessage()
                ));
        }
    }
}
