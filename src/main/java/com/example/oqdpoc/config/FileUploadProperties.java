package com.example.oqdpoc.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.stereotype.Component;
import org.springframework.util.unit.DataSize;

@Component
@ConfigurationProperties(prefix = "app.upload")
public class FileUploadProperties {
    private int maxFiles;
    
    @NestedConfigurationProperty
    private final Multipart multipart = new Multipart();
    
    public int getMaxFiles() {
        return maxFiles;
    }
    
    public void setMaxFiles(int maxFiles) {
        this.maxFiles = maxFiles;
    }
    
    public Multipart getMultipart() {
        return multipart;
    }
    
    public static class Multipart {
        private DataSize maxFileSize;
        private DataSize maxRequestSize;
        
        public DataSize getMaxFileSize() {
            return maxFileSize;
        }
        
        public void setMaxFileSize(DataSize maxFileSize) {
            this.maxFileSize = maxFileSize;
        }
        
        public DataSize getMaxRequestSize() {
            return maxRequestSize;
        }
        
        public void setMaxRequestSize(DataSize maxRequestSize) {
            this.maxRequestSize = maxRequestSize;
        }
    }
}
