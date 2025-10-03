package com.example.oqdpoc.model;

/**
 * Represents an item in a checklist that can be converted to a PDF.
 */
public class ChecklistItem {
    private String title;
    private boolean completed;
    private String description;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
