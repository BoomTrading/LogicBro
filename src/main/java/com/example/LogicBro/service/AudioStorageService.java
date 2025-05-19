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

@Service
public class AudioStorageService {
    private static final Logger logger = LoggerFactory.getLogger(AudioStorageService.class);
    private static final String ALLOWED_FILE_TYPE = "audio/mpeg";
    
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

        if (!ALLOWED_FILE_TYPE.equals(file.getContentType())) {
            throw new StorageException("Only MP3 files are allowed");
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
            return filename;
            
        } catch (IOException e) {
            throw new StorageException("Failed to store file " + filename, e);
        }
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