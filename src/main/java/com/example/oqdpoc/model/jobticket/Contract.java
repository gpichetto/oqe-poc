package com.example.oqdpoc.model.jobticket;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.example.oqdpoc.util.CustomOffsetDateTimeDeserializer;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * Represents a contract with its associated details and terms.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Contract {
    private String id;
    private String name;
    private String description;
    private String status;
    private OffsetDateTime startDate;
    private OffsetDateTime endDate;
    private List<SignatureResponse> signatures;
    private Template template;
    private PdfOptions pdfOptions;
    private Metadata metadata;

    // Getters and Setters with @JsonProperty annotations
    @JsonProperty("id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @JsonProperty("startDate")
    @JsonDeserialize(using = CustomOffsetDateTimeDeserializer.class)
    public OffsetDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(OffsetDateTime startDate) {
        this.startDate = startDate;
    }

    @JsonProperty("endDate")
    @JsonDeserialize(using = CustomOffsetDateTimeDeserializer.class)
    public OffsetDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(OffsetDateTime endDate) {
        this.endDate = endDate;
    }

    @JsonProperty("signatures")
    public List<SignatureResponse> getSignatures() {
        return signatures;
    }

    public void setSignatures(List<SignatureResponse> signatures) {
        this.signatures = signatures;
    }

    @JsonProperty("template")
    public Template getTemplate() {
        return template;
    }

    public void setTemplate(Template template) {
        this.template = template;
    }

    @JsonProperty("pdfOptions")
    public PdfOptions getPdfOptions() {
        return pdfOptions;
    }

    public void setPdfOptions(PdfOptions pdfOptions) {
        this.pdfOptions = pdfOptions;
    }

    @JsonProperty("metadata")
    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }
}
