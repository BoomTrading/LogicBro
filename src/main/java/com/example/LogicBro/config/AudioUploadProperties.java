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

    /**
     * Maximum file size allowed for upload (in bytes)
     */
    private long maxFileSize = 50 * 1024 * 1024; // 50MB default

    /**
     * Allowed audio file extensions
     */
    private String[] allowedExtensions = {".mp3", ".wav", ".ogg", ".aac", ".m4a"};

    /**
     * Whether to analyze files immediately after upload
     */
    private boolean autoAnalyze = true;

    /**
     * Temporary directory for processing files
     */
    private String tempDir = "temp";

    /**
     * Directory for storing analysis results
     */
    private String analysisPath = "analysis";

    /**
     * Path to the FFmpeg executable
     */
    private String ffmpegPath = "ffmpeg"; // Default assumes ffmpeg is in PATH

    /**
     * Path to the FFprobe executable
     */
    private String ffprobePath = "ffprobe"; // Default assumes ffprobe is in PATH

    public String getAudioPath() {
        return audioPath;
    }

    public void setAudioPath(String audioPath) {
        this.audioPath = audioPath;
    }

    public long getMaxFileSize() {
        return maxFileSize;
    }

    public void setMaxFileSize(long maxFileSize) {
        this.maxFileSize = maxFileSize;
    }

    public String[] getAllowedExtensions() {
        return allowedExtensions;
    }

    public void setAllowedExtensions(String[] allowedExtensions) {
        this.allowedExtensions = allowedExtensions;
    }

    public boolean isAutoAnalyze() {
        return autoAnalyze;
    }

    public void setAutoAnalyze(boolean autoAnalyze) {
        this.autoAnalyze = autoAnalyze;
    }

    public String getTempDir() {
        return tempDir;
    }

    public void setTempDir(String tempDir) {
        this.tempDir = tempDir;
    }

    public String getAnalysisPath() {
        return analysisPath;
    }

    public void setAnalysisPath(String analysisPath) {
        this.analysisPath = analysisPath;
    }

    public String getFfmpegPath() {
        return ffmpegPath;
    }

    public void setFfmpegPath(String ffmpegPath) {
        this.ffmpegPath = ffmpegPath;
    }

    public String getFfprobePath() {
        return ffprobePath;
    }

    public void setFfprobePath(String ffprobePath) {
        this.ffprobePath = ffprobePath;
    }
}
