package com.example.oqdpoc.model.jobticket;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Represents a section in a job ticket that contains a group of related questions.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Section {
    private String id;
    private String title;
    private String description;
    private List<Question> questions;

    /**
     * Gets the unique identifier of the section.
     * @return the section ID
     */
    @JsonProperty("id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the title of the section.
     * @return the section title
     */
    @JsonProperty("title")
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Gets the description of the section.
     * @return the section description
     */
    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the list of questions in this section.
     * @return list of questions
     */
    @JsonProperty("questions")
    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    @Override
    public String toString() {
        return "Section{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", questions=" + (questions != null ? questions.size() : 0) +
                '}';
    }
}
