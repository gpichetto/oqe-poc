package com.example.oqdpoc.validator;

import com.example.oqdpoc.model.ChecklistItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ChecklistItemValidatorTest {

    @InjectMocks
    private ChecklistItemValidator validator;

    private ChecklistItem validItem;
    private Errors errors;

    @BeforeEach
    void setUp() {
        validItem = new ChecklistItem();
        validItem.setTitle("Test Title");
        validItem.setDescription("Test Description");
        validItem.setCompleted(false);
    }

    @Test
    void supports_ChecklistItemClass_ReturnsTrue() {
        // Act & Assert
        assertTrue(validator.supports(ChecklistItem.class));
    }

    @Test
    void supports_NonChecklistItemClass_ReturnsFalse() {
        // Act & Assert
        assertFalse(validator.supports(Object.class));
    }

    @Test
    void validate_WithValidItem_NoErrors() {
        // Arrange
        errors = new BeanPropertyBindingResult(validItem, "validItem");

        // Act
        validator.validate(validItem, errors);

        // Assert
        assertFalse(errors.hasErrors());
    }

    @Test
    void validate_WithNullTitle_AddsError() {
        // Arrange
        validItem.setTitle(null);
        errors = new BeanPropertyBindingResult(validItem, "invalidItem");

        // Act
        validator.validate(validItem, errors);

        // Assert
        assertTrue(errors.hasErrors());
        assertEquals(1, errors.getErrorCount());
        assertNotNull(errors.getFieldError("title"));
        assertEquals("title.required", errors.getFieldError("title").getCode());
        assertEquals("Title is required", errors.getFieldError("title").getDefaultMessage());
    }

    @Test
    void validate_WithEmptyTitle_AddsError() {
        // Arrange
        validItem.setTitle("");
        errors = new BeanPropertyBindingResult(validItem, "invalidItem");

        // Act
        validator.validate(validItem, errors);

        // Assert
        assertTrue(errors.hasErrors());
        assertEquals(1, errors.getErrorCount());
        assertNotNull(errors.getFieldError("title"));
    }

    @Test
    void validate_WithBlankTitle_AddsError() {
        // Arrange
        validItem.setTitle("   ");
        errors = new BeanPropertyBindingResult(validItem, "invalidItem");

        // Act
        validator.validate(validItem, errors);

        // Assert
        assertTrue(errors.hasErrors());
        assertEquals(1, errors.getErrorCount());
        assertNotNull(errors.getFieldError("title"));
    }

    @Test
    void validate_WithNullDescription_AddsError() {
        // Arrange
        validItem.setDescription(null);
        errors = new BeanPropertyBindingResult(validItem, "invalidItem");

        // Act
        validator.validate(validItem, errors);

        // Assert
        assertTrue(errors.hasErrors());
        assertEquals(1, errors.getErrorCount());
        assertNotNull(errors.getFieldError("description"));
        assertEquals("description.required", errors.getFieldError("description").getCode());
        assertEquals("Description is required", errors.getFieldError("description").getDefaultMessage());
    }

    @Test
    void validate_WithEmptyDescription_AddsError() {
        // Arrange
        validItem.setDescription("");
        errors = new BeanPropertyBindingResult(validItem, "invalidItem");

        // Act
        validator.validate(validItem, errors);

        // Assert
        assertTrue(errors.hasErrors());
        assertEquals(1, errors.getErrorCount());
        assertNotNull(errors.getFieldError("description"));
    }

    @Test
    void validate_WithBlankDescription_AddsError() {
        // Arrange
        validItem.setDescription("   ");
        errors = new BeanPropertyBindingResult(validItem, "invalidItem");

        // Act
        validator.validate(validItem, errors);

        // Assert
        assertTrue(errors.hasErrors());
        assertEquals(1, errors.getErrorCount());
        assertNotNull(errors.getFieldError("description"));
    }

    @Test
    void validate_WithAllFieldsInvalid_AddsAllErrors() {
        // Arrange
        validItem.setTitle(null);
        validItem.setDescription(null);
        errors = new BeanPropertyBindingResult(validItem, "invalidItem");

        // Act
        validator.validate(validItem, errors);

        // Assert
        assertTrue(errors.hasErrors());
        assertEquals(2, errors.getErrorCount());
        assertNotNull(errors.getFieldError("title"));
        assertNotNull(errors.getFieldError("description"));
    }
}
