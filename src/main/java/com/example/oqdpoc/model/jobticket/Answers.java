package com.example.oqdpoc.model.jobticket;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.example.oqdpoc.util.CustomOffsetDateTimeDeserializer;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * Contains the answers and responses for a job ticket.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Answers {
    private String id;
    private String title;
    private String description;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private String knowledgeBaseId;
    private String header;
    private String cover;
    private String footer;
    private PdfOptions pdfOptions;
    private Template template;
    private boolean isFinish;
    private List<Section> sections;
    private Metadata metadata;

    // Getters and Setters with @JsonProperty annotations
    @JsonProperty("_id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("title")
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty("createdAt")
    @JsonDeserialize(using = CustomOffsetDateTimeDeserializer.class)
    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @JsonProperty("updatedAt")
    @JsonDeserialize(using = CustomOffsetDateTimeDeserializer.class)
    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @JsonProperty("knowledgeBaseId")
    public String getKnowledgeBaseId() {
        return knowledgeBaseId;
    }

    public void setKnowledgeBaseId(String knowledgeBaseId) {
        this.knowledgeBaseId = knowledgeBaseId;
    }

    @JsonProperty("header")
    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    @JsonProperty("cover")
    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    @JsonProperty("footer")
    public String getFooter() {
        return footer;
    }

    public void setFooter(String footer) {
        this.footer = footer;
    }

    @JsonProperty("pdfOptions")
    public PdfOptions getPdfOptions() {
        return pdfOptions;
    }

    public void setPdfOptions(PdfOptions pdfOptions) {
        this.pdfOptions = pdfOptions;
    }

    @JsonProperty("template")
    public Template getTemplate() {
        return template;
    }

    public void setTemplate(Template template) {
        this.template = template;
    }

    @JsonProperty("isFinish")
    public boolean isFinish() {
        return isFinish;
    }

    public void setFinish(boolean finish) {
        isFinish = finish;
    }

    @JsonProperty("sections")
    public List<Section> getSections() {
        return sections;
    }

    public void setSections(List<Section> sections) {
        this.sections = sections;
    }

    @JsonProperty("metadata")
    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }
}
