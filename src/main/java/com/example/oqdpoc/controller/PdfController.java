package com.example.oqdpoc.controller;

import com.example.oqdpoc.model.jobticket.JobTicket;
import com.example.oqdpoc.model.shortworkperiod.WorkOrder;

import java.time.LocalDateTime;
import java.util.ArrayList;

import com.example.oqdpoc.service.PdfGenerationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
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
import org.springframework.web.bind.annotation.*;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import com.example.oqdpoc.config.Resilience4jConfig;

import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.time.format.DateTimeFormatter;
@RestController
@RequestMapping("/api/pdf")
public class PdfController {
    private static final Logger log = LoggerFactory.getLogger(PdfController.class);
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private static final String LOGO_CLASSPATH = "static/images/thales-logo.png";

    private final PdfGenerationService pdfGenerationService;
    private final TemplateEngine templateEngine;
    private final ObjectMapper objectMapper;
    public PdfController(
            PdfGenerationService pdfGenerationService,
            TemplateEngine templateEngine,
            ObjectMapper objectMapper) {
        this.pdfGenerationService = pdfGenerationService;
        this.templateEngine = templateEngine;
        this.objectMapper = objectMapper;
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
    @Retry(name = Resilience4jConfig.PDF_GENERATION_RETRY)
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
        
        // Load logo from classpath and embed as data URL for portability
        try {
            ClassPathResource logoResource = new ClassPathResource(LOGO_CLASSPATH);
            if (logoResource.exists()) {
                try (java.io.InputStream in = logoResource.getInputStream()) {
                    byte[] bytes = in.readAllBytes();
                    String base64 = Base64.getEncoder().encodeToString(bytes);
                    String dataUrl = "data:image/png;base64," + base64;
                    context.setVariable("logoUrl", dataUrl);
                }
            } else {
                log.warn("Logo resource not found on classpath: {}", LOGO_CLASSPATH);
            }
        } catch (Exception e) {
            log.warn("Failed to load logo from classpath: {}", e.getMessage());
        }
        context.setVariable("currentDate", java.time.LocalDateTime.now());

        // Process the template with the data
        String html = templateEngine.process("jobTicket", context);

        // Generate PDF using the service
        byte[] pdfBytes = pdfGenerationService.generatePdfWithWorkOrder(html);

        // Return the PDF as a response
        return createPdfResponse(pdfBytes, Collections.emptyList(), acceptHeader);
    }
}