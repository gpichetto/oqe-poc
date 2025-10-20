package com.example.oqdpoc.service;

import com.example.oqdpoc.exception.FileTypeValidationException;
import com.example.oqdpoc.validator.FileTypeValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ImageProcessingService {
    private static final Logger log = LoggerFactory.getLogger(ImageProcessingService.class);
    private final FileTypeValidator fileTypeValidator;

    public ImageProcessingService(FileTypeValidator fileTypeValidator) {
        this.fileTypeValidator = fileTypeValidator;
    }

    /**
     * Processes a list of image files, validates them, and converts them to base64 data URLs
     *
     * @param imageFiles List of image files to process
     * @return List of base64-encoded image data URLs
     * @throws FileTypeValidationException if any file fails validation
     */
    public List<String> processImages(List<MultipartFile> imageFiles) throws FileTypeValidationException {
        if (imageFiles == null || imageFiles.isEmpty()) {
            return Collections.emptyList();
        }
        
        // Validate all files first
        fileTypeValidator.validateImageFiles(imageFiles);
        
        return imageFiles.stream()
            .filter(Objects::nonNull)
            .filter(file -> !file.isEmpty())
            .map(this::convertToBase64)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }
    
    /**
     * Converts a single MultipartFile to a base64 data URL
     *
     * @param file The file to convert
     * @return Base64 data URL string or null if conversion fails
     */
    private String convertToBase64(MultipartFile file) {
        try {
            String mimeType = file.getContentType();
            String base64 = Base64.getEncoder().encodeToString(file.getBytes());
            return "data:" + mimeType + ";base64," + base64;
        } catch (IOException e) {
            log.warn("Error processing image file: {}", file.getOriginalFilename(), e);
            return null;
        }
    }
}
