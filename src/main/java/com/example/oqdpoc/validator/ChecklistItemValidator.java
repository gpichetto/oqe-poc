package com.example.oqdpoc.validator;

import com.example.oqdpoc.model.ChecklistItem;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class ChecklistItemValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return ChecklistItem.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ChecklistItem item = (ChecklistItem) target;
        
        // Check title
        if (item.getTitle() == null || item.getTitle().trim().isEmpty()) {
            errors.rejectValue("title", "title.required", "Title is required");
        }
        
        // Check description
        if (item.getDescription() == null || item.getDescription().trim().isEmpty()) {
            errors.rejectValue("description", "description.required", "Description is required");
        }
    }
}
