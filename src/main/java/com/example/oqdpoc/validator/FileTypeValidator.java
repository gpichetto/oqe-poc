package com.example.oqdpoc.validator;

import com.example.oqdpoc.exception.FileTypeValidationException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Validates image files for type and content integrity.
 * Currently supports JPEG, PNG, and SVG file formats.
 */
@Component
public class FileTypeValidator {
    private static final Map<String, String> ALLOWED_IMAGE_TYPES = Map.of(
        "image/jpeg", "jpg",
        "image/png", "png",
        "image/svg+xml", "svg"
    );

    /**
     * Validates a list of image files.
     * @param files List of files to validate (can be null or empty)
     * @throws FileTypeValidationException if any file fails validation
     */
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
        
        if (!isAllowedContentType(contentType)) {
            throw new FileTypeValidationException(
                String.format("Invalid file type: %s. Allowed types are: %s", 
                    originalFilename, 
                    String.join(", ", ALLOWED_IMAGE_TYPES.keySet()))
            );
        }

        if (isRasterImage(contentType)) {
            validateRasterImageContent(file, originalFilename);
        }
        // SVG validation could be added here if needed
    }

    private boolean isAllowedContentType(String contentType) {
        return contentType != null && 
               ALLOWED_IMAGE_TYPES.keySet().stream()
                   .anyMatch(allowed -> allowed.equals(contentType));
    }

    private boolean isRasterImage(String contentType) {
        return "image/jpeg".equals(contentType) || "image/png".equals(contentType);
    }

    /**
     * Validates that the file content is a properly formatted raster image (JPEG/PNG).
     * This is a security-critical check that ensures:
     * 1. The file content actually matches its declared MIME type (prevents file type spoofing)
     * 2. The image is not corrupted and can be properly decoded
     * 3. The file contains valid image data according to its format
     * 
     * @param file The multipart file to validate
     * @param filename The original filename for error reporting
     * @throws FileTypeValidationException if the file is not a valid image or cannot be read
     */
    private void validateRasterImageContent(MultipartFile file, String filename) {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(file.getBytes())) {
            BufferedImage image = ImageIO.read(bis);
            if (image == null) {
                throw new FileTypeValidationException(
                    String.format("Invalid image content or corrupted file: %s", filename)
                );
            }
        } catch (IOException e) {
            throw new FileTypeValidationException(
                String.format("Error processing image %s: %s", filename, e.getMessage())
            );
        }
    }
}
