package com.example.oqdpoc.model.jobticket;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.example.oqdpoc.util.CustomOffsetDateTimeDeserializer;
import java.time.OffsetDateTime;

/**
 * Represents a digital signature response with associated metadata.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SignatureResponse {
    private String id;
    private String name;
    private String role;
    private String file;
    private String email;
    private String status;
    private OffsetDateTime signedAt;
    private String ipAddress;
    private String signatureType;
    private String signatureImage;
    private String verificationCode;

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

    @JsonProperty("role")
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @JsonProperty("file")
    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    @JsonProperty("email")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @JsonProperty("signedAt")
    @JsonDeserialize(using = CustomOffsetDateTimeDeserializer.class)
    public OffsetDateTime getSignedAt() {
        return signedAt;
    }

    public void setSignedAt(OffsetDateTime signedAt) {
        this.signedAt = signedAt;
    }

    @JsonProperty("ipAddress")
    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    @JsonProperty("signatureType")
    public String getSignatureType() {
        return signatureType;
    }

    public void setSignatureType(String signatureType) {
        this.signatureType = signatureType;
    }

    @JsonProperty("signatureImage")
    public String getSignatureImage() {
        return signatureImage;
    }

    public void setSignatureImage(String signatureImage) {
        this.signatureImage = signatureImage;
    }

    @JsonProperty("verificationCode")
    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }
}
