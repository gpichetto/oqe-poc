package com.example.oqdpoc.validator;

import com.example.oqdpoc.exception.FileTypeValidationException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Component
public class FileTypeValidator {
    private static final List<String> ALLOWED_IMAGE_TYPES = List.of(
        "image/jpeg", 
        "image/png", 
        "image/svg+xml"
    );

    public void validateImageFiles(List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            return;
        }

        files.stream()
             .filter(Objects::nonNull)
             .filter(file -> !file.isEmpty())
             .forEach(this::validateSingleFile);
    }

    private void validateSingleFile(MultipartFile file) {
        String contentType = file.getContentType();
        String originalFilename = file.getOriginalFilename();

        validateContentType(contentType, originalFilename);
        
        if (contentType != null && 
            (contentType.startsWith("image/jpeg") || contentType.startsWith("image/png"))) {
            validateImageContent(file, originalFilename);
        }
    }

    private void validateContentType(String contentType, String filename) {
        if (contentType == null || ALLOWED_IMAGE_TYPES.stream().noneMatch(contentType::startsWith)) {
            throw new FileTypeValidationException(
                String.format("Invalid file type: %s. Only JPG, PNG, and SVG files are allowed.", 
                    filename)
            );
        }
    }

    private void validateImageContent(MultipartFile file, String filename) {
        try {
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(file.getBytes()));
            if (image == null) {
                throw new FileTypeValidationException(
                    String.format("Invalid image content: %s", filename)
                );
            }
        } catch (IOException e) {
            throw new FileTypeValidationException(
                String.format("Error processing image %s: %s", filename, e.getMessage())
            );
        }
    }
}
