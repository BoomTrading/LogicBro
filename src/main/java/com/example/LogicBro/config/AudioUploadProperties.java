package com.example.LogicBro.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.constraints.NotBlank;

@Validated
@ConfigurationProperties(prefix = "logicbro.upload")
public class AudioUploadProperties {
    /**
     * Directory path where audio files will be stored.
     * This path must be accessible and writable by the application.
     */
    @NotBlank(message = "Audio upload path must not be empty")
    private String audioPath;

    public String getAudioPath() {
        return audioPath;
    }

    public void setAudioPath(String audioPath) {
        this.audioPath = audioPath;
    }
}
