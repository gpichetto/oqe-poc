package com.example.oqdpoc.controller;

import com.example.oqdpoc.exception.PdfGenerationException;
import com.example.oqdpoc.model.ChecklistItem;
import com.example.oqdpoc.model.jobticket.JobTicket;
import com.example.oqdpoc.service.PdfGenerationService;
import com.example.oqdpoc.validator.ChecklistItemValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import static org.springframework.http.MediaType.APPLICATION_PDF_VALUE;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.time.format.DateTimeFormatter;

@Validated
@RestController
@RequestMapping("/api/pdf")
public class PdfController {
    private static final Logger log = LoggerFactory.getLogger(PdfController.class);
    private static final String PDF_FILENAME = "checklist.pdf";
    private static final String JOB_TICKET_PDF_FILENAME = "job-ticket.pdf";
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private final ChecklistItemValidator checklistItemValidator;
    private final PdfGenerationService pdfGenerationService;
    private final TemplateEngine templateEngine;
    private final ObjectMapper objectMapper;

    public PdfController(
            ChecklistItemValidator checklistItemValidator,
            PdfGenerationService pdfGenerationService,
            TemplateEngine templateEngine,
            ObjectMapper objectMapper) {
        this.checklistItemValidator = checklistItemValidator;
        this.pdfGenerationService = pdfGenerationService;
        this.templateEngine = templateEngine;
        this.objectMapper = objectMapper;
    }

    /**
     * Creates a standardized error response for validation errors
     */
    private ResponseEntity<Map<String, Object>> createValidationErrorResponse(BindingResult bindingResult) {
        log.warn("Validation errors found: {}", bindingResult.getAllErrors());
        
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now().format(TIMESTAMP_FORMATTER));
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
            @RequestBody(required = false) List<ChecklistItem> checklistItems,
            @RequestHeader(value = "Accept", required = false) String acceptHeader) {

        // Check for null request body
        if (checklistItems == null) {
            log.warn("Null checklist items provided");
            return ResponseEntity.badRequest().body(
                    Map.of(
                            "timestamp", LocalDateTime.now().format(TIMESTAMP_FORMATTER),
                            "status", HttpStatus.BAD_REQUEST.value(),
                            "error", "Bad Request",
                            "message", "Request body is required"
                    )
            );
        }

        // Check for empty list
        if (checklistItems.isEmpty()) {
            log.warn("Empty checklist items provided");
            return ResponseEntity.badRequest().body(
                    Map.of(
                            "timestamp", LocalDateTime.now().format(TIMESTAMP_FORMATTER),
                            "status", HttpStatus.BAD_REQUEST.value(),
                            "error", "Bad Request",
                            "message", "At least one checklist item is required"
                    )
            );
        }

        for (int i = 0; i < checklistItems.size(); i++) {
            ChecklistItem item = checklistItems.get(i);
            DataBinder binder = new DataBinder(item, "checklistItems[" + i + "]");
            BindingResult bindingResult = binder.getBindingResult();
            checklistItemValidator.validate(item, bindingResult);

            if (bindingResult.hasErrors()) {
                return createValidationErrorResponse(bindingResult);
            }
        }

        // Generate the PDF
        try {
            byte[] pdfBytes = pdfGenerationService.generatePdf(checklistItems);

            // Return the appropriate response based on the Accept header
            if (acceptHeader != null && acceptHeader.contains(MediaType.APPLICATION_JSON_VALUE)) {
                // Return JSON with base64-encoded PDF
                String base64Pdf = Base64.getEncoder().encodeToString(pdfBytes);
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(Map.of(
                                "status", "success",
                                "pdfBase64", base64Pdf
                        ));
            } else {
                // Return PDF directly
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_PDF)
                        .header(HttpHeaders.CONTENT_DISPOSITION,
                                "inline; filename=\"" + PDF_FILENAME + "\"")
                        .body(pdfBytes);
            }

        } catch (PdfGenerationException e) {
            log.error("Error generating PDF: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of(
                            "timestamp", LocalDateTime.now().format(TIMESTAMP_FORMATTER),
                            "status", HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "error", "PDF Generation Failed",
                            "message", "Failed to generate PDF: " + e.getMessage()
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
                    "timestamp", LocalDateTime.now().format(TIMESTAMP_FORMATTER),
                    "status", HttpStatus.BAD_REQUEST.value(),
                    "error", "Bad Request",
                    "message", "Job ticket data is required"
                )
            );
        }
        
        try {
            // Generate the PDF with retry capability
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
                    "timestamp", LocalDateTime.now().format(TIMESTAMP_FORMATTER),
                    "status", HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "error", "PDF Generation Failed",
                    "message", "Failed to generate Job Ticket PDF: " + e.getMessage()
                ));
        } catch (Exception e) {
            log.error("Unexpected error generating Job Ticket PDF", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of(
                    "timestamp", LocalDateTime.now().format(TIMESTAMP_FORMATTER),
                    "status", HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "error", "Internal Server Error",
                    "message", "An unexpected error occurred: " + e.getMessage()
                ));
        }
    }
    
    /**
     * Renders a Job Ticket with attached images into a PDF document
     * 
     * @param acceptHeader Optional Accept header to determine response format
     * @return ResponseEntity containing either the PDF bytes or a JSON response with base64-encoded PDF
     */
    @PostMapping(value = "/render-job-ticket-with-images", 
        produces = {APPLICATION_PDF_VALUE, MediaType.APPLICATION_JSON_VALUE},
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> renderJobTicketWithImages(
            @RequestPart("jsonFile") MultipartFile jsonFile,
            @RequestPart(value = "imageFiles", required = false) List<MultipartFile> imageFiles,
            @RequestHeader(value = "Accept", required = false) String acceptHeader) {
        
        log.info("Received request to generate job ticket PDF");
        log.debug("JSON file: {}, size: {} bytes", jsonFile.getOriginalFilename(), jsonFile.getSize());
        log.debug("Image files count: {}", imageFiles != null ? imageFiles.size() : 0);
        log.debug("Accept header: {}", acceptHeader);
        
        log.info("Received request to generate job ticket PDF");
        log.debug("JSON file: {}, size: {} bytes", jsonFile.getOriginalFilename(), jsonFile.getSize());
        log.debug("Image files count: {}", imageFiles != null ? imageFiles.size() : 0);
        log.debug("Accept header: {}", acceptHeader);
        
        // Check if the required jsonFile is present and not empty
        if (jsonFile == null || jsonFile.isEmpty()) {
            log.warn("Missing required file part 'jsonFile'");
            return ResponseEntity.badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of(
                    "timestamp", LocalDateTime.now().format(TIMESTAMP_FORMATTER),
                    "status", HttpStatus.BAD_REQUEST.value(),
                    "error", "Bad Request",
                    "message", "Missing required file part 'jsonFile'"
                ));
        }
        
        // Combine all files for validation (including the required jsonFile)
        List<MultipartFile> allFiles = new ArrayList<>();
        allFiles.add(jsonFile);
        if (imageFiles != null) {
            allFiles.addAll(imageFiles);
        }
        
        // Validate total number of files doesn't exceed the limit
        if (allFiles.size() > 25) {
            return ResponseEntity.badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of(
                    "timestamp", LocalDateTime.now().format(TIMESTAMP_FORMATTER),
                    "status", HttpStatus.BAD_REQUEST.value(),
                    "error", "Bad Request",
                    "message", "Maximum 25 files are allowed per request (including the JSON file)"
                ));
        }
        
        try {
            log.info("Received request to generate job ticket PDF");
            
            // 1. Parse JSON file
            JobTicket jobTicket = objectMapper.readValue(jsonFile.getBytes(), JobTicket.class);
            
            // 2. Process images (if any)
            List<String> base64Images = new ArrayList<>();
            if (imageFiles != null && !imageFiles.isEmpty()) {
                base64Images = imageFiles.stream()
                    .filter(file -> !file.isEmpty())
                    .map(file -> {
                        try {
                            String mimeType = file.getContentType();
                            if (mimeType == null) {
                                mimeType = "image/jpeg"; // default to jpeg if not specified
                            }
                            String base64 = Base64.getEncoder().encodeToString(file.getBytes());
                            return "data:" + mimeType + ";base64," + base64;
                        } catch (IOException e) {
                            log.warn("Error processing image file: {}. Error: {}", 
                                   file.getOriginalFilename(), 
                                   e.getMessage());
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            }
            
            // 3. Generate HTML with Thymeleaf
            Context context = new Context();
            context.setVariable("jobTicket", jobTicket);
            context.setVariable("images", base64Images);
            
            String html = templateEngine.process("jobTicketWithImages", context);
            
            // 4. Generate PDF
            byte[] pdfBytes = pdfGenerationService.generateJobTicketPdfWithImages(html);
            
            // 5. Return response
            try {
                log.debug("Preparing response. Accept header: {}", acceptHeader);
                boolean preferJson = StringUtils.hasText(acceptHeader) && 
                                  acceptHeader.contains(MediaType.APPLICATION_JSON_VALUE);
                
                if (preferJson) {
                    log.debug("Creating JSON response with base64-encoded PDF");
                    String base64Pdf = Base64.getEncoder().encodeToString(pdfBytes);
                    Map<String, Object> response = Map.of(
                        "status", "success",
                        "imageCount", base64Images.size(),
                        "pdfBase64", base64Pdf
                    );
                    log.debug("JSON response prepared successfully");
                    return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(response);
                } else {
                    log.debug("Creating PDF response");
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_PDF);
                    headers.setContentDispositionFormData("inline", "job-ticket.pdf");
                    headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
                    headers.setContentLength(pdfBytes.length);
                    log.debug("Response headers set. Content-Length: {}", pdfBytes.length);
                    return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
                }
            } catch (Exception e) {
                log.error("Error creating response: {}", e.getMessage(), e);
                throw new RuntimeException("Failed to create response: " + e.getMessage(), e);
            }
            
        } catch (IOException e) {
            log.error("Error processing uploaded files: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of(
                    "timestamp", LocalDateTime.now().format(TIMESTAMP_FORMATTER),
                    "status", HttpStatus.BAD_REQUEST.value(),
                    "error", "Invalid Request",
                    "message", "Error processing uploaded files: " + e.getMessage(),
                    "details", e.toString()
                ));
        } catch (PdfGenerationException e) {
            log.error("Error generating PDF: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of(
                    "timestamp", LocalDateTime.now().format(TIMESTAMP_FORMATTER),
                    "status", HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "error", "PDF Generation Failed",
                    "message", "Failed to generate PDF: " + e.getMessage(),
                    "details", e.toString(),
                    "stackTrace", Arrays.stream(e.getStackTrace())
                                      .map(StackTraceElement::toString)
                                      .collect(Collectors.toList())
                ));
        } catch (Exception e) {
            log.error("Unexpected error generating Job Ticket PDF", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of(
                    "timestamp", LocalDateTime.now().format(TIMESTAMP_FORMATTER),
                    "status", HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "error", "Internal Server Error",
                    "message", "An unexpected error occurred: " + e.getMessage()
                ));
        }
    }
}
