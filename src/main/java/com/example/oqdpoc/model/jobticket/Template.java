package com.example.oqdpoc.model.jobticket;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a template configuration in the job ticket system.
 * Contains template-related settings and configurations.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Template {
    
    @JsonProperty("carboneio")
    private Object carboneio;  // Can be null as per the JSON example

    /**
     * Gets the carboneio configuration object.
     * @return The carboneio configuration or null if not set
     */
    public Object getCarboneio() {
        return carboneio;
    }

    /**
     * Sets the carboneio configuration object.
     * @param carboneio The carboneio configuration to set
     */
    public void setCarboneio(Object carboneio) {
        this.carboneio = carboneio;
    }

    @Override
    public String toString() {
        return "Template{" +
                "carboneio=" + carboneio +
                '}';
    }
}
