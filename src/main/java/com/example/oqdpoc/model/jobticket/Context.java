package com.example.oqdpoc.model.jobticket;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.example.oqdpoc.util.CustomOffsetDateTimeDeserializer;
import java.time.OffsetDateTime;

/**
 * Represents the execution context of a job ticket, containing runtime information
 * and references to related entities like assets.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Context {
    private OffsetDateTime startedAt;
    private String language;
    private String sourceWorkOrderId;
    private Asset asset;
    private String location;
    private String userId;
    private String workOrderType;

    @JsonProperty("startedAt")
    @JsonDeserialize(using = CustomOffsetDateTimeDeserializer.class)
    public OffsetDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(OffsetDateTime startedAt) {
        this.startedAt = startedAt;
    }

    @JsonProperty("language")
    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    @JsonProperty("sourceWorkOrderId")
    public String getSourceWorkOrderId() {
        return sourceWorkOrderId;
    }

    public void setSourceWorkOrderId(String sourceWorkOrderId) {
        this.sourceWorkOrderId = sourceWorkOrderId;
    }

    @JsonProperty("asset")
    public Asset getAsset() {
        return asset;
    }

    public void setAsset(Asset asset) {
        this.asset = asset;
    }

    @JsonProperty("location")
    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @JsonProperty("userId")
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @JsonProperty("workOrderType")
    public String getWorkOrderType() {
        return workOrderType;
    }

    public void setWorkOrderType(String workOrderType) {
        this.workOrderType = workOrderType;
    }
}
