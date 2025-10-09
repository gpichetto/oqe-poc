package com.example.oqdpoc.model.jobticket;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.example.oqdpoc.util.CustomOffsetDateTimeDeserializer;
import java.time.OffsetDateTime;
import java.util.Map;

/**
 * Represents a job ticket which is the main entity for work orders or service tickets.
 * Contains all the information needed to track and complete a specific job.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JobTicket {
    private String id;
    private String checklistId;
    private String userId;
    private OffsetDateTime createdAt;
    private User user;
    private Answers answers;
    private Map<String, Object> flatAnswers;
    private Context context;
    private String contractId;
    private Contract contract;

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("checklistId")
    public String getChecklistId() {
        return checklistId;
    }

    public void setChecklistId(String checklistId) {
        this.checklistId = checklistId;
    }

    @JsonProperty("userId")
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @JsonProperty("createdAt")
    @JsonDeserialize(using = CustomOffsetDateTimeDeserializer.class)
    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @JsonProperty("user")
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @JsonProperty("answers")
    public Answers getAnswers() {
        return answers;
    }

    public void setAnswers(Answers answers) {
        this.answers = answers;
    }

    @JsonProperty("flatAnswers")
    public Map<String, Object> getFlatAnswers() {
        return flatAnswers;
    }

    public void setFlatAnswers(Map<String, Object> flatAnswers) {
        this.flatAnswers = flatAnswers;
    }

    @JsonProperty("context")
    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @JsonProperty("contractId")
    public String getContractId() {
        return contractId;
    }

    public void setContractId(String contractId) {
        this.contractId = contractId;
    }

    @JsonProperty("contract")
    public Contract getContract() {
        return contract;
    }

    public void setContract(Contract contract) {
        this.contract = contract;
        // Keep contractId in sync with contract.id if needed
        if (contract != null && contract.getId() != null) {
            this.contractId = contract.getId();
        }
    }
}
