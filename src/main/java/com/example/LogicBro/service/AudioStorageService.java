package com.example.LogicBro.service;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.LogicBro.config.AudioUploadProperties;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class AudioStorageService {
    private static final Logger logger = LoggerFactory.getLogger(AudioStorageService.class);
    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
        "audio/mpeg",
        "audio/mp3", 
        "audio/wav",
        "audio/x-wav",
        "audio/ogg",
        "audio/aac",
        "audio/mp4",
        "audio/m4a"
    );
    
    private final AudioUploadProperties properties;

    public AudioStorageService(AudioUploadProperties properties) {
        this.properties = properties;
    }

    private String getAudioUploadPath() {
        return properties.getAudioPath();
    }
    
    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(Paths.get(getAudioUploadPath()));
            logger.info("Created audio upload directory at: {}", getAudioUploadPath());
        } catch (IOException e) {
            throw new StorageException("Could not initialize storage location", e);
        }
    }
    
    public String storeAudio(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new StorageException("Failed to store empty file");
        }

        if (!isValidAudioFile(file)) {
            throw new StorageException("Invalid audio file type. Allowed types: " + ALLOWED_CONTENT_TYPES);
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new StorageException("Failed to store file with null or empty filename");
        }

        String filename = StringUtils.cleanPath(originalFilename);
        
        if (filename.contains("..")) {
            throw new StorageException("Cannot store file with relative path outside current directory: " + filename);
        }
        
        try {
            Path destinationPath = Paths.get(getAudioUploadPath()).resolve(filename).normalize();
            
            if (!destinationPath.getParent().equals(Paths.get(getAudioUploadPath()).normalize())) {
                throw new StorageException("Cannot store file outside current directory");
            }
            
            Files.copy(file.getInputStream(), destinationPath, StandardCopyOption.REPLACE_EXISTING);
            logger.info("Successfully stored file: {}", filename);
            return destinationPath.toString();
            
        } catch (IOException e) {
            throw new StorageException("Failed to store file " + filename, e);
        }
    }
    
    private boolean isValidAudioFile(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null) {
            return false;
        }
        return ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase());
    }
}

class StorageException extends RuntimeException {
    public StorageException(String message) {
        super(message);
    }
    
    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }
}