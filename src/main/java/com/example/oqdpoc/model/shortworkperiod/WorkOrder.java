package com.example.oqdpoc.model.shortworkperiod;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

public class WorkOrder {
    @JsonProperty("_id")
    private String id;
    
    @JsonProperty("_translangcode")
    private String translationCode;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    @JsonProperty("actfinish")
    private LocalDateTime actualFinish;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    @JsonProperty("actstart")
    private LocalDateTime actualStart;
    
    @JsonProperty("assetnum")
    private String assetNum;
    
    @JsonProperty("description")
    private String description;
    
    @JsonProperty("description_longdescription")
    private String longDescription;
    
    @JsonProperty("href")
    private String href;
    
    @JsonProperty("jpnum")
    private String jobPlanNumber;
    
    @JsonProperty("owner")
    private String owner;
    
    @JsonProperty("parent")
    private String parent;
    
    @JsonProperty("pcphys")
    private String physicalLocation;
    
    @JsonProperty("plusareference")
    private String referenceType;
    
    @JsonProperty("plusareferenceid")
    private String referenceId;
    
    @JsonProperty("plusmworkperf")
    private String workPerformedBy;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    @JsonProperty("schedfinish")
    private LocalDateTime scheduledFinish;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    @JsonProperty("schedstart")
    private LocalDateTime scheduledStart;
    
    @JsonProperty("siteid")
    private String siteId;
    
    @JsonProperty("status")
    private String status;
    
    @JsonProperty("status_description")
    private String statusDescription;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    @JsonProperty("targcompdate")
    private LocalDateTime targetCompletionDate;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    @JsonProperty("targstartdate")
    private LocalDateTime targetStartDate;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    @JsonProperty("thacalldate")
    private LocalDateTime callDate;
    
    @JsonProperty("thacfto")
    private String cfto;
    
    @JsonProperty("thadesc")
    private String additionalDescription;
    
    @JsonProperty("thamtcpln")
    private String amountCompletedPlan;
    
    @JsonProperty("than1n9")
    private String n1n9;
    
    @JsonProperty("thand")
    private String and;
    
    @JsonProperty("thansc")
    private Boolean answerComplete;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    @JsonProperty("thantfdate")
    private LocalDateTime answerDate;
    
    @JsonProperty("thantffrom")
    private String answerFrom;
    
    @JsonProperty("thantfto")
    private String answerTo;
    
    @JsonProperty("thaopdef")
    private String operationDefinition;
    
    @JsonProperty("vendor")
    private String vendor;
    
    @JsonProperty("wonum")
    private String wonum;
    
    @JsonProperty("worktype")
    private String workType;

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTranslationCode() {
        return translationCode;
    }

    public void setTranslationCode(String translationCode) {
        this.translationCode = translationCode;
    }

    public LocalDateTime getActualFinish() {
        return actualFinish;
    }

    public void setActualFinish(LocalDateTime actualFinish) {
        this.actualFinish = actualFinish;
    }

    public LocalDateTime getActualStart() {
        return actualStart;
    }

    public void setActualStart(LocalDateTime actualStart) {
        this.actualStart = actualStart;
    }

    public String getAssetNum() {
        return assetNum;
    }

    public void setAssetNum(String assetNum) {
        this.assetNum = assetNum;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLongDescription() {
        return longDescription;
    }

    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getJobPlanNumber() {
        return jobPlanNumber;
    }

    public void setJobPlanNumber(String jobPlanNumber) {
        this.jobPlanNumber = jobPlanNumber;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public String getPhysicalLocation() {
        return physicalLocation;
    }

    public void setPhysicalLocation(String physicalLocation) {
        this.physicalLocation = physicalLocation;
    }

    public String getReferenceType() {
        return referenceType;
    }

    public void setReferenceType(String referenceType) {
        this.referenceType = referenceType;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public String getWorkPerformedBy() {
        return workPerformedBy;
    }

    public void setWorkPerformedBy(String workPerformedBy) {
        this.workPerformedBy = workPerformedBy;
    }

    public LocalDateTime getScheduledFinish() {
        return scheduledFinish;
    }

    public void setScheduledFinish(LocalDateTime scheduledFinish) {
        this.scheduledFinish = scheduledFinish;
    }

    public LocalDateTime getScheduledStart() {
        return scheduledStart;
    }

    public void setScheduledStart(LocalDateTime scheduledStart) {
        this.scheduledStart = scheduledStart;
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusDescription() {
        return statusDescription;
    }

    public void setStatusDescription(String statusDescription) {
        this.statusDescription = statusDescription;
    }

    public LocalDateTime getTargetCompletionDate() {
        return targetCompletionDate;
    }

    public void setTargetCompletionDate(LocalDateTime targetCompletionDate) {
        this.targetCompletionDate = targetCompletionDate;
    }

    public LocalDateTime getTargetStartDate() {
        return targetStartDate;
    }

    public void setTargetStartDate(LocalDateTime targetStartDate) {
        this.targetStartDate = targetStartDate;
    }

    public LocalDateTime getCallDate() {
        return callDate;
    }

    public void setCallDate(LocalDateTime callDate) {
        this.callDate = callDate;
    }

    public String getCfto() {
        return cfto;
    }

    public void setCfto(String cfto) {
        this.cfto = cfto;
    }

    public String getAdditionalDescription() {
        return additionalDescription;
    }

    public void setAdditionalDescription(String additionalDescription) {
        this.additionalDescription = additionalDescription;
    }

    public String getAmountCompletedPlan() {
        return amountCompletedPlan;
    }

    public void setAmountCompletedPlan(String amountCompletedPlan) {
        this.amountCompletedPlan = amountCompletedPlan;
    }

    public String getN1n9() {
        return n1n9;
    }

    public void setN1n9(String n1n9) {
        this.n1n9 = n1n9;
    }

    public String getAnd() {
        return and;
    }

    public void setAnd(String and) {
        this.and = and;
    }

    public Boolean getAnswerComplete() {
        return answerComplete;
    }

    public void setAnswerComplete(Boolean answerComplete) {
        this.answerComplete = answerComplete;
    }

    public LocalDateTime getAnswerDate() {
        return answerDate;
    }

    public void setAnswerDate(LocalDateTime answerDate) {
        this.answerDate = answerDate;
    }

    public String getAnswerFrom() {
        return answerFrom;
    }

    public void setAnswerFrom(String answerFrom) {
        this.answerFrom = answerFrom;
    }

    public String getAnswerTo() {
        return answerTo;
    }

    public void setAnswerTo(String answerTo) {
        this.answerTo = answerTo;
    }

    public String getOperationDefinition() {
        return operationDefinition;
    }

    public void setOperationDefinition(String operationDefinition) {
        this.operationDefinition = operationDefinition;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getWonum() {
        return wonum;
    }

    public void setWonum(String wonum) {
        this.wonum = wonum;
    }

    public String getWorkType() {
        return workType;
    }

    public void setWorkType(String workType) {
        this.workType = workType;
    }
}
