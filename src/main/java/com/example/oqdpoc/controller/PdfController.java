package com.example.oqdpoc.controller;

import com.example.oqdpoc.exception.PdfGenerationException;
import com.example.oqdpoc.model.shortworkperiod.WorkOrder;
import com.example.oqdpoc.model.jobticket.JobTicket;
import com.fasterxml.jackson.databind.JsonNode;

import java.time.LocalDateTime;
import java.util.ArrayList;
import com.example.oqdpoc.service.ImageProcessingService;
import com.example.oqdpoc.service.PdfGenerationService;
import com.example.oqdpoc.validator.FileTypeValidator;
import com.example.oqdpoc.exception.FileTypeValidationException;
import com.fasterxml.jackson.core.JsonProcessingException;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import io.swagger.v3.oas.annotations.Hidden;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import com.example.oqdpoc.config.Resilience4jConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.time.format.DateTimeFormatter;

@Validated
@RestController
@RequestMapping("/api/pdf")
public class PdfController {
    private static final Logger log = LoggerFactory.getLogger(PdfController.class);
    private static final String JOB_TICKET_PDF_FILENAME = "job-ticket.pdf";
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private final PdfGenerationService pdfGenerationService;
    private final TemplateEngine templateEngine;
    private final ObjectMapper objectMapper;
    private final FileTypeValidator fileTypeValidator;
    private final ImageProcessingService imageProcessingService;

    public PdfController(
            PdfGenerationService pdfGenerationService,
            TemplateEngine templateEngine,
            ObjectMapper objectMapper,
            FileTypeValidator fileTypeValidator,
            ImageProcessingService imageProcessingService) {
        this.pdfGenerationService = pdfGenerationService;
        this.templateEngine = templateEngine;
        this.objectMapper = objectMapper;
        this.fileTypeValidator = fileTypeValidator;
        this.imageProcessingService = imageProcessingService;
    }

    /**
     * Creates a standardized error response for validation errors
     */
    private ResponseEntity<Map<String, Object>> createErrorResponse(String message) {
        return ResponseEntity.badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of(
                        "timestamp", LocalDateTime.now().format(TIMESTAMP_FORMATTER),
                        "status", HttpStatus.BAD_REQUEST.value(),
                        "error", "Bad Request",
                        "message", message
                ));
    }

    /**
     * Creates a PDF response based on the accept header
     *
     * @param pdfBytes     The PDF content as bytes
     * @param base64Images List of base64-encoded images
     * @param acceptHeader The Accept header from the request
     * @return ResponseEntity containing either the PDF or a JSON response
     */
    private ResponseEntity<?> createPdfResponse(byte[] pdfBytes, List<String> base64Images, String acceptHeader) {
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
    }

    /**
     * Renders a Job Ticket JSON into a PDF document
     *
     * @param jobTicket    The job ticket data to render
     * @param acceptHeader Optional Accept header to determine response format
     * @return ResponseEntity containing either the PDF bytes or a JSON response with base64-encoded PDF
     */
    @PostMapping(value = "/render-job-ticket",
            produces = {APPLICATION_PDF_VALUE, MediaType.APPLICATION_JSON_VALUE},
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @RateLimiter(name = Resilience4jConfig.PDF_GENERATION_RATE_LIMITER)
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
    @Retry(name = Resilience4jConfig.PDF_GENERATION_RETRY)
    @RateLimiter(name = Resilience4jConfig.PDF_GENERATION_RATE_LIMITER)
    @Hidden // Hide from default OpenAPI docs as we're using programmatic configuration
    public ResponseEntity<?> renderJobTicketWithImages(
            @RequestPart("jobTicket") String jobTicketJson,
            @RequestPart(value = "imageFiles", required = false) List<MultipartFile> imageFiles,
            @RequestHeader(value = "Accept", required = false) String acceptHeader) {

        log.info("Received request to generate job ticket PDF");

        // Check if the jobTicket JSON is present and not empty
        if (!StringUtils.hasText(jobTicketJson)) {
            log.warn("Missing or empty required part 'jobTicket'");
            return createErrorResponse("Missing or empty required part 'jobTicket'");
        }

        // Validate image files if any are provided
        if (imageFiles != null && !imageFiles.isEmpty()) {
            try {
                fileTypeValidator.validateImageFiles(imageFiles);
            } catch (FileTypeValidationException e) {
                log.warn("File validation failed: {}", e.getMessage());
                return createErrorResponse(e.getMessage());
            }
        }

        try {
            log.info("Received request to generate job ticket PDF");

            // 1. Parse JSON payload
            JobTicket jobTicket;
            try {
                jobTicket = objectMapper.readValue(jobTicketJson, JobTicket.class);
            } catch (JsonProcessingException e) {
                log.error("Failed to parse job ticket JSON: {}", e.getMessage());
                return createErrorResponse("Invalid JSON in jobTicket: " + e.getMessage());
            }

            // 2. Process images (if any) using the ImageProcessingService
            List<String> base64Images = Collections.emptyList();
            if (imageFiles != null && !imageFiles.isEmpty()) {
                try {
                    base64Images = imageProcessingService.processImages(imageFiles);
                    log.debug("Successfully processed {} images", base64Images.size());
                } catch (FileTypeValidationException e) {
                    log.warn("Image validation failed: {}", e.getMessage());
                    return createErrorResponse(e.getMessage());
                }
            }

            // 3. Generate HTML with Thymeleaf
            Context context = new Context();
            context.setVariable("jobTicket", jobTicket);
            context.setVariable("images", base64Images);

            String html = templateEngine.process("jobTicketWithImages", context);

            // 4. Generate PDF
            byte[] pdfBytes = pdfGenerationService.generateJobTicketPdfWithImages(html);

            // 5. Return response
            return createPdfResponse(pdfBytes, base64Images, acceptHeader);
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

    /**
     * Renders a Job Ticket with optional Short Work Period details into a PDF document
     *
     * @param jobTicketJson       Required JSON string containing the job ticket data
     * @param shortWorkPeriodJson Optional JSON string containing the short work period data
     * @param acceptHeader        Optional Accept header to determine response format
     * @return ResponseEntity containing either the PDF bytes or a JSON response with base64-encoded PDF
     */
    @PostMapping(
            value = "/render-job-ticket-short-work-period",
            produces = {APPLICATION_PDF_VALUE, MediaType.APPLICATION_JSON_VALUE},
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @RateLimiter(name = Resilience4jConfig.PDF_GENERATION_RATE_LIMITER)
    public ResponseEntity<?> renderJobTicketAndWorkOrders(
            @RequestPart("jobTicket") String jobTicketJson,
            @RequestPart(value = "shortWorkPeriod", required = false) String shortWorkPeriodJson,
            @RequestHeader(value = "Accept", required = false) String acceptHeader) {

        // 1. Parse JobTicket JSON
        JobTicket jobTicket;
        try {
            jobTicket = objectMapper.readValue(jobTicketJson, JobTicket.class);
            log.debug("Successfully parsed JobTicket with ID: {}", jobTicket.getId());
        } catch (JsonProcessingException e) {
            log.error("Failed to parse jobTicket JSON: {}", e.getMessage());
            return createErrorResponse("Invalid jobTicket JSON: " + e.getMessage());
        }

        // 2. Parse optional WorkOrders JSON if provided
        List<WorkOrder> workOrders = new ArrayList<>();
        if (StringUtils.hasText(shortWorkPeriodJson)) {
            try {
                JsonNode rootNode = objectMapper.readTree(shortWorkPeriodJson);
                JsonNode memberNode = rootNode.path("member");
                if (memberNode.isArray()) {
                    for (JsonNode node : memberNode) {
                        WorkOrder workOrder = objectMapper.treeToValue(node, WorkOrder.class);
                        workOrders.add(workOrder);
                    }
                    log.debug("Successfully parsed {} work orders", workOrders.size());
                }
            } catch (JsonProcessingException e) {
                log.warn("Failed to parse work orders JSON, continuing without it: {}", e.getMessage());
                // Continue without work orders data as it's optional
            }
        } else {
            log.debug("No work orders data provided");
        }

        // Find the work order that matches the job ticket's work order number
        WorkOrder workOrderForReport = workOrders.stream()
            .filter(workOrder -> workOrder.getWonum()
                .equals(jobTicket.getAnswers().getMetadata().getAdditional().getWorkOrder().getWorkOrderNum()))
            .findFirst()
            .orElse(null);

        // Create Thymeleaf context and add variables
        Context context = new Context();
        context.setVariable("jobTicket", jobTicket);
        context.setVariable("workOrderForReport", workOrderForReport);


        // Process the template with the data
        String html = templateEngine.process("jobTicket-V2", context);

        // Generate PDF using the service
        byte[] pdfBytes = pdfGenerationService.generatePdfWithWorkOrder(html);

        // Return the PDF as a response
        return createPdfResponse(pdfBytes, Collections.emptyList(), acceptHeader);
    }
}