package com.example.oqdpoc.model.jobticket;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Configuration options for PDF generation.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PdfOptions {
    private boolean newPageOnSection;
    private List<Integer> pageMargins;
    private String orientation;
    private String pageSize;
    private boolean includeHeader;
    private boolean includeFooter;
    private boolean includePageNumbers;

    @JsonProperty("newPageOnSection")
    public boolean isNewPageOnSection() {
        return newPageOnSection;
    }

    public void setNewPageOnSection(boolean newPageOnSection) {
        this.newPageOnSection = newPageOnSection;
    }

    @JsonProperty("pageMargins")
    public List<Integer> getPageMargins() {
        return pageMargins;
    }

    public void setPageMargins(List<Integer> pageMargins) {
        this.pageMargins = pageMargins;
    }

    @JsonProperty("orientation")
    public String getOrientation() {
        return orientation;
    }

    public void setOrientation(String orientation) {
        this.orientation = orientation;
    }

    @JsonProperty("pageSize")
    public String getPageSize() {
        return pageSize;
    }

    public void setPageSize(String pageSize) {
        this.pageSize = pageSize;
    }

    @JsonProperty("includeHeader")
    public boolean isIncludeHeader() {
        return includeHeader;
    }

    public void setIncludeHeader(boolean includeHeader) {
        this.includeHeader = includeHeader;
    }

    @JsonProperty("includeFooter")
    public boolean isIncludeFooter() {
        return includeFooter;
    }

    public void setIncludeFooter(boolean includeFooter) {
        this.includeFooter = includeFooter;
    }

    @JsonProperty("includePageNumbers")
    public boolean isIncludePageNumbers() {
        return includePageNumbers;
    }

    public void setIncludePageNumbers(boolean includePageNumbers) {
        this.includePageNumbers = includePageNumbers;
    }
}
