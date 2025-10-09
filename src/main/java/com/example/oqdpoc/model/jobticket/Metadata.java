package com.example.oqdpoc.model.jobticket;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Represents metadata associated with a job ticket or document.
 * Contains part numbers, codes, and additional job-related information.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Metadata {
    private List<String> partNumbers;
    private List<String> codes;
    private Additional additional;
    
    // Getters and setters
    @JsonProperty("partNumbers")
    public List<String> getPartNumbers() {
        return partNumbers;
    }

    public void setPartNumbers(List<String> partNumbers) {
        this.partNumbers = partNumbers;
    }

    @JsonProperty("codes")
    public List<String> getCodes() {
        return codes;
    }

    public void setCodes(List<String> codes) {
        this.codes = codes;
    }

    @JsonProperty("additional")
    public Additional getAdditional() {
        return additional;
    }

    public void setAdditional(Additional additional) {
        this.additional = additional;
    }
    
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Additional {
        private String codes;
        private List<String> onChecklistCompletionActions;
        private Asset asset;
        private WorkOrder workOrder;

        @JsonProperty("codes")
        public String getCodes() {
            return codes;
        }

        public void setCodes(String codes) {
            this.codes = codes;
        }

        @JsonProperty("onChecklistCompletionActions")
        public List<String> getOnChecklistCompletionActions() {
            return onChecklistCompletionActions;
        }

        public void setOnChecklistCompletionActions(List<String> onChecklistCompletionActions) {
            this.onChecklistCompletionActions = onChecklistCompletionActions;
        }

        @JsonProperty("asset")
        public Asset getAsset() {
            return asset;
        }

        public void setAsset(Asset asset) {
            this.asset = asset;
        }

        @JsonProperty("workOrder")
        public WorkOrder getWorkOrder() {
            return workOrder;
        }

        public void setWorkOrder(WorkOrder workOrder) {
            this.workOrder = workOrder;
        }
    }
    
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Asset {
        private String assetNum;
        private String partNumber;
        private String serialNumber;
        private String designation;
        private String commercialName;
        private String lcn;
        private String description;
        private String status;
        private String bdistatus;
        private String location;
        private String orgid;
        private String siteid;
        private int quantity;
        private String parent;
        private String plusaasofdate;

        // Getters and setters
        @JsonProperty("assetNum")
        public String getAssetNum() { return assetNum; }
        public void setAssetNum(String assetNum) { this.assetNum = assetNum; }

        @JsonProperty("partNumber")
        public String getPartNumber() { return partNumber; }
        public void setPartNumber(String partNumber) { this.partNumber = partNumber; }

        @JsonProperty("serialNumber")
        public String getSerialNumber() { return serialNumber; }
        public void setSerialNumber(String serialNumber) { this.serialNumber = serialNumber; }

        @JsonProperty("designation")
        public String getDesignation() { return designation; }
        public void setDesignation(String designation) { this.designation = designation; }

        @JsonProperty("commercialName")
        public String getCommercialName() { return commercialName; }
        public void setCommercialName(String commercialName) { this.commercialName = commercialName; }

        @JsonProperty("lcn")
        public String getLcn() { return lcn; }
        public void setLcn(String lcn) { this.lcn = lcn; }

        @JsonProperty("description")
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        @JsonProperty("status")
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        @JsonProperty("bdistatus")
        public String getBdistatus() { return bdistatus; }
        public void setBdistatus(String bdistatus) { this.bdistatus = bdistatus; }

        @JsonProperty("location")
        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }

        @JsonProperty("orgid")
        public String getOrgid() { return orgid; }
        public void setOrgid(String orgid) { this.orgid = orgid; }

        @JsonProperty("siteid")
        public String getSiteid() { return siteid; }
        public void setSiteid(String siteid) { this.siteid = siteid; }

        @JsonProperty("quantity")
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }

        @JsonProperty("parent")
        public String getParent() { return parent; }
        public void setParent(String parent) { this.parent = parent; }

        @JsonProperty("plusaasofdate")
        public String getPlusaasofdate() { return plusaasofdate; }
        public void setPlusaasofdate(String plusaasofdate) { this.plusaasofdate = plusaasofdate; }
    }
    
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class WorkOrder {
        private int workOrderId;
        private String title;
        private String description;
        private String location;
        private String status;
        private String deadline;
        private String scheduledStart;
        private String realStart;
        private String realEnd;
        private String workOrderNum;
        private WorkOrderAsset asset;
        private Object contract;
        private String reportedBy;
        private String assignedTo;
        private String assignedToGroup;
        private String createdAt;
        private WorkOrderType type;
        private Object jobPlan;
        private String orgid;
        private String reference;
        private String referenceId;
        private List<Object> tasks;
        private List<Object> worklogs;
        private List<Attribute> attributes;
        private List<Object> attachments;
        private Vendor vendor;
        private WorkOrderPackage _package;
        private String parentWorkOrderNum;
        private Object completionRate;

        // Getters and setters for WorkOrder
        @JsonProperty("workOrderId")
        public int getWorkOrderId() { return workOrderId; }
        public void setWorkOrderId(int workOrderId) { this.workOrderId = workOrderId; }

        @JsonProperty("title")
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        @JsonProperty("description")
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        @JsonProperty("location")
        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }

        @JsonProperty("status")
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        @JsonProperty("deadline")
        public String getDeadline() { return deadline; }
        public void setDeadline(String deadline) { this.deadline = deadline; }

        @JsonProperty("scheduledStart")
        public String getScheduledStart() { return scheduledStart; }
        public void setScheduledStart(String scheduledStart) { this.scheduledStart = scheduledStart; }

        @JsonProperty("realStart")
        public String getRealStart() { return realStart; }
        public void setRealStart(String realStart) { this.realStart = realStart; }

        @JsonProperty("realEnd")
        public String getRealEnd() { return realEnd; }
        public void setRealEnd(String realEnd) { this.realEnd = realEnd; }

        @JsonProperty("workOrderNum")
        public String getWorkOrderNum() { return workOrderNum; }
        public void setWorkOrderNum(String workOrderNum) { this.workOrderNum = workOrderNum; }

        @JsonProperty("asset")
        public WorkOrderAsset getAsset() { return asset; }
        public void setAsset(WorkOrderAsset asset) { this.asset = asset; }

        @JsonProperty("contract")
        public Object getContract() { return contract; }
        public void setContract(Object contract) { this.contract = contract; }

        @JsonProperty("reportedBy")
        public String getReportedBy() { return reportedBy; }
        public void setReportedBy(String reportedBy) { this.reportedBy = reportedBy; }

        @JsonProperty("assignedTo")
        public String getAssignedTo() { return assignedTo; }
        public void setAssignedTo(String assignedTo) { this.assignedTo = assignedTo; }

        @JsonProperty("assignedToGroup")
        public String getAssignedToGroup() { return assignedToGroup; }
        public void setAssignedToGroup(String assignedToGroup) { this.assignedToGroup = assignedToGroup; }

        @JsonProperty("createdAt")
        public String getCreatedAt() { return createdAt; }
        public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

        @JsonProperty("type")
        public WorkOrderType getType() { return type; }
        public void setType(WorkOrderType type) { this.type = type; }

        @JsonProperty("jobPlan")
        public Object getJobPlan() { return jobPlan; }
        public void setJobPlan(Object jobPlan) { this.jobPlan = jobPlan; }

        @JsonProperty("orgid")
        public String getOrgid() { return orgid; }
        public void setOrgid(String orgid) { this.orgid = orgid; }

        @JsonProperty("reference")
        public String getReference() { return reference; }
        public void setReference(String reference) { this.reference = reference; }

        @JsonProperty("referenceId")
        public String getReferenceId() { return referenceId; }
        public void setReferenceId(String referenceId) { this.referenceId = referenceId; }

        @JsonProperty("tasks")
        public List<Object> getTasks() { return tasks; }
        public void setTasks(List<Object> tasks) { this.tasks = tasks; }

        @JsonProperty("worklogs")
        public List<Object> getWorklogs() { return worklogs; }
        public void setWorklogs(List<Object> worklogs) { this.worklogs = worklogs; }

        @JsonProperty("attributes")
        public List<Attribute> getAttributes() { return attributes; }
        public void setAttributes(List<Attribute> attributes) { this.attributes = attributes; }

        @JsonProperty("attachments")
        public List<Object> getAttachments() { return attachments; }
        public void setAttachments(List<Object> attachments) { this.attachments = attachments; }

        @JsonProperty("vendor")
        public Vendor getVendor() { return vendor; }
        public void setVendor(Vendor vendor) { this.vendor = vendor; }

        @JsonProperty("package")
        public WorkOrderPackage getPackage() { return _package; }
        public void setPackage(WorkOrderPackage _package) { this._package = _package; }

        @JsonProperty("parentWorkOrderNum")
        public String getParentWorkOrderNum() { return parentWorkOrderNum; }
        public void setParentWorkOrderNum(String parentWorkOrderNum) { this.parentWorkOrderNum = parentWorkOrderNum; }

        @JsonProperty("completionRate")
        public Object getCompletionRate() { return completionRate; }
        public void setCompletionRate(Object completionRate) { this.completionRate = completionRate; }
    }
    
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class WorkOrderAsset {
        private String assetuid;
        private String assetNum;
        private String ancestor;
        private String serialNumber;
        private String partNumber;
        private String status;
        private String designation;
        private String description;
        private String commercialName;
        private String bdistatus;
        private String plusaasofdate;
        private String model;
        private String variation;
        private String lcn;
        private String label;
        private String positionName;
        private Integer quantity;
        private String siteid;
        private String orgid;
        private String parent;
        private String location;
        private String locationDetails;
        private Boolean hasChildren;
        private Object relations;
        private Object configurationItems;

        // Getters and setters
        @JsonProperty("assetuid")
        public String getAssetuid() { return assetuid; }
        public void setAssetuid(String assetuid) { this.assetuid = assetuid; }

        @JsonProperty("assetNum")
        public String getAssetNum() { return assetNum; }
        public void setAssetNum(String assetNum) { this.assetNum = assetNum; }

        @JsonProperty("ancestor")
        public String getAncestor() { return ancestor; }
        public void setAncestor(String ancestor) { this.ancestor = ancestor; }

        @JsonProperty("serialNumber")
        public String getSerialNumber() { return serialNumber; }
        public void setSerialNumber(String serialNumber) { this.serialNumber = serialNumber; }

        @JsonProperty("partNumber")
        public String getPartNumber() { return partNumber; }
        public void setPartNumber(String partNumber) { this.partNumber = partNumber; }

        @JsonProperty("status")
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        @JsonProperty("designation")
        public String getDesignation() { return designation; }
        public void setDesignation(String designation) { this.designation = designation; }

        @JsonProperty("description")
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        @JsonProperty("commercialName")
        public String getCommercialName() { return commercialName; }
        public void setCommercialName(String commercialName) { this.commercialName = commercialName; }

        @JsonProperty("bdistatus")
        public String getBdistatus() { return bdistatus; }
        public void setBdistatus(String bdistatus) { this.bdistatus = bdistatus; }

        @JsonProperty("plusaasofdate")
        public String getPlusaasofdate() { return plusaasofdate; }
        public void setPlusaasofdate(String plusaasofdate) { this.plusaasofdate = plusaasofdate; }

        @JsonProperty("model")
        public String getModel() { return model; }
        public void setModel(String model) { this.model = model; }

        @JsonProperty("variation")
        public String getVariation() { return variation; }
        public void setVariation(String variation) { this.variation = variation; }

        @JsonProperty("lcn")
        public String getLcn() { return lcn; }
        public void setLcn(String lcn) { this.lcn = lcn; }

        @JsonProperty("label")
        public String getLabel() { return label; }
        public void setLabel(String label) { this.label = label; }

        @JsonProperty("positionName")
        public String getPositionName() { return positionName; }
        public void setPositionName(String positionName) { this.positionName = positionName; }

        @JsonProperty("quantity")
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }

        @JsonProperty("siteid")
        public String getSiteid() { return siteid; }
        public void setSiteid(String siteid) { this.siteid = siteid; }

        @JsonProperty("orgid")
        public String getOrgid() { return orgid; }
        public void setOrgid(String orgid) { this.orgid = orgid; }

        @JsonProperty("parent")
        public String getParent() { return parent; }
        public void setParent(String parent) { this.parent = parent; }

        @JsonProperty("location")
        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }

        @JsonProperty("locationDetails")
        public String getLocationDetails() { return locationDetails; }
        public void setLocationDetails(String locationDetails) { this.locationDetails = locationDetails; }

        @JsonProperty("hasChildren")
        public Boolean getHasChildren() { return hasChildren; }
        public void setHasChildren(Boolean hasChildren) { this.hasChildren = hasChildren; }

        @JsonProperty("relations")
        public Object getRelations() { return relations; }
        public void setRelations(Object relations) { this.relations = relations; }

        @JsonProperty("configurationItems")
        public Object getConfigurationItems() { return configurationItems; }
        public void setConfigurationItems(Object configurationItems) { this.configurationItems = configurationItems; }
    }
    
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class WorkOrderType {
        private String workType;
        private String description;

        @JsonProperty("workType")
        public String getWorkType() { return workType; }
        public void setWorkType(String workType) { this.workType = workType; }

        @JsonProperty("description")
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }
    
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Attribute {
        private int id;
        private String attribute;
        private String type;
        private List<String> value;

        @JsonProperty("id")
        public int getId() { return id; }
        public void setId(int id) { this.id = id; }

        @JsonProperty("attribute")
        public String getAttribute() { return attribute; }
        public void setAttribute(String attribute) { this.attribute = attribute; }

        @JsonProperty("type")
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        @JsonProperty("value")
        public List<String> getValue() { return value; }
        public void setValue(List<String> value) { this.value = value; }
    }
    
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Vendor {
        private String id;
        private String name;

        @JsonProperty("id")
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        @JsonProperty("name")
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }
    
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class WorkOrderPackage {
        private int id;
        private String assetnum;
        private String description;
        private String num;
        private String status;

        @JsonProperty("id")
        public int getId() { return id; }
        public void setId(int id) { this.id = id; }

        @JsonProperty("assetnum")
        public String getAssetnum() { return assetnum; }
        public void setAssetnum(String assetnum) { this.assetnum = assetnum; }

        @JsonProperty("description")
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        @JsonProperty("num")
        public String getNum() { return num; }
        public void setNum(String num) { this.num = num; }

        @JsonProperty("status")
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
}
