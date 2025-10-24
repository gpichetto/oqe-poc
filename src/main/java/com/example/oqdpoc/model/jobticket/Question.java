package com.example.oqdpoc.model.jobticket;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.example.oqdpoc.util.CustomOffsetDateTimeDeserializer;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

/**
 * Represents a question in a job ticket with its properties and responses.
 * This class handles various question types including text, multiple choice, date, and signature questions.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Question {
    private String uuid;
    private Integer id;
    private String type;
    private String title;
    private boolean mandatory;
    private boolean attachment;
    private boolean comment;
    private boolean hideInReport;
    private String hint;
    private boolean responseBelow;
    private String commentContent;
    private Map<String, Object> metadata;
    private Object response;
    private OffsetDateTime signatureDate;
    private boolean signatureAuto;
    private String format;
    private String defaultValue;
    private List<String> choices;
    private boolean multipleAnswer;
    private List<String> documentUrls;
    private List<String> attachmentContents;

    @JsonProperty("uuid")
    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @JsonProperty("id")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @JsonProperty("type")
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @JsonProperty("title")
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @JsonProperty("mandatory")
    public boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }

    @JsonProperty("attachment")
    public boolean isAttachment() {
        return attachment;
    }

    public void setAttachment(boolean attachment) {
        this.attachment = attachment;
    }

    @JsonProperty("comment")
    public boolean isComment() {
        return comment;
    }

    public void setComment(boolean comment) {
        this.comment = comment;
    }

    @JsonProperty("hideInReport")
    public boolean isHideInReport() {
        return hideInReport;
    }

    public void setHideInReport(boolean hideInReport) {
        this.hideInReport = hideInReport;
    }

    @JsonProperty("hint")
    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    @JsonProperty("responseBelow")
    public boolean isResponseBelow() {
        return responseBelow;
    }

    public void setResponseBelow(boolean responseBelow) {
        this.responseBelow = responseBelow;
    }

    @JsonProperty("commentContent")
    public String getCommentContent() {
        return commentContent;
    }

    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
    }

    @JsonProperty("metadata")
    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    @JsonProperty("response")
    public Object getResponse() {
        return response;
    }

    public void setResponse(Object response) {
        this.response = response;
    }

    @JsonProperty("signatureDate")
    @JsonDeserialize(using = CustomOffsetDateTimeDeserializer.class)
    public OffsetDateTime getSignatureDate() {
        return signatureDate;
    }

    public void setSignatureDate(OffsetDateTime signatureDate) {
        this.signatureDate = signatureDate;
    }

    @JsonProperty("signatureAuto")
    public boolean isSignatureAuto() {
        return signatureAuto;
    }

    public void setSignatureAuto(boolean signatureAuto) {
        this.signatureAuto = signatureAuto;
    }

    @JsonProperty("format")
    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    @JsonProperty("defaultValue")
    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    @JsonProperty("choices")
    public List<String> getChoices() {
        return choices;
    }

    public void setChoices(List<String> choices) {
        this.choices = choices;
    }

    @JsonProperty("multipleAnswer")
    public boolean isMultipleAnswer() {
        return multipleAnswer;
    }

    public void setMultipleAnswer(boolean multipleAnswer) {
        this.multipleAnswer = multipleAnswer;
    }

    @JsonProperty("documentUrls")
    public List<String> getDocumentUrls() {
        return documentUrls;
    }

    public void setDocumentUrls(List<String> documentUrls) {
        this.documentUrls = documentUrls;
    }

    @JsonProperty("attachmentContents")
    public List<String> getAttachmentContents() {
        return attachmentContents;
    }

    public void setAttachmentContents(List<String> attachmentContents) {
        this.attachmentContents = attachmentContents;
    }

    @Override
    public String toString() {
        return "Question{" +
                "uuid='" + uuid + '\'' +
                ", id=" + id +
                ", type='" + type + '\'' +
                ", title='" + title + '\'' +
                ", mandatory=" + mandatory +
                ", response=" + response +
                '}';
    }
}
