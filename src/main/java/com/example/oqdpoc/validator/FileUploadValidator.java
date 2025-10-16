package com.example.oqdpoc.validator;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.validation.BeanPropertyBindingResult;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Validates file upload constraints.
 * - Ensures the total number of files (JSON + images) doesn't exceed the maximum allowed
 * - Note: File size validation is handled by Spring's multipart configuration
 */
@Component
public class FileUploadValidator {
    
    private static final int MAX_TOTAL_FILES = 20;
    
    /**
     * Validates that the total number of files doesn't exceed the limit
     * @param jsonFile The required JSON file
     * @param imageFiles Optional list of image files
     * @return BindingResult with validation errors, or null if validation passes
     */
    public void validateFiles(MultipartFile jsonFile, List<MultipartFile> imageFiles, Errors errors) {
        if (jsonFile == null || jsonFile.isEmpty()) {
            errors.rejectValue("jsonFile", "required", "JSON file is required");
            return;
        }
        
        int totalFiles = 1;
        if (imageFiles != null) {
            // Only count non-empty image files
            long validImageFiles = imageFiles.stream()
                .filter(file -> file != null && !file.isEmpty())
                .count();
            totalFiles += validImageFiles;
        }
        
        if (totalFiles > MAX_TOTAL_FILES) {
            errors.reject("files.max", 
                "Maximum " + MAX_TOTAL_FILES + " files are allowed (1 JSON file + 1 image)");
        }
    }
}
