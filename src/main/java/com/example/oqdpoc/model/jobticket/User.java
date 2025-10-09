package com.example.oqdpoc.model.jobticket;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a user in the system with basic identification information.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User {
    private String id;
    private String email;

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
