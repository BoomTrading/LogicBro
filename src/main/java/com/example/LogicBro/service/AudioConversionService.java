package com.example.LogicBro.service;

import com.example.LogicBro.config.AudioUploadProperties;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class AudioConversionService {
    
    private static final Logger logger = LoggerFactory.getLogger(AudioConversionService.class);
    
    private final AudioUploadProperties audioUploadProperties;
    private FFmpeg ffmpeg;
    private FFprobe ffprobe;
    
    @Autowired
    public AudioConversionService(AudioUploadProperties audioUploadProperties) {
        this.audioUploadProperties = audioUploadProperties;
        initializeFFmpeg();
    }
    
    private void initializeFFmpeg() {
        try {
            ffmpeg = new FFmpeg(audioUploadProperties.getFfmpegPath());
            ffprobe = new FFprobe(audioUploadProperties.getFfprobePath());
            logger.info("FFmpeg initialized successfully");
        } catch (IOException e) {
            logger.warn("FFmpeg not found. Audio format conversion will be disabled. Error: {}", e.getMessage());
        }
    }
    
    public boolean isFFmpegAvailable() {
        return ffmpeg != null && ffprobe != null;
    }
    
    public File convertToWav(File inputFile) throws IOException {
        if (!isFFmpegAvailable()) {
            throw new RuntimeException("FFmpeg is not available. Please install FFmpeg or use supported audio formats (WAV, AIFF, AU).");
        }
        
        // Create temporary WAV file
        Path tempDir = Paths.get(System.getProperty("java.io.tmpdir"));
        String fileName = inputFile.getName();
        String baseName = fileName.substring(0, fileName.lastIndexOf('.'));
        Path outputPath = tempDir.resolve(baseName + "_converted.wav");
        
        try {
            FFmpegBuilder builder = new FFmpegBuilder()
                .setInput(inputFile.getAbsolutePath())
                .overrideOutputFiles(true)
                .addOutput(outputPath.toString())
                .setFormat("wav")
                .setAudioCodec("pcm_s16le")
                .setAudioSampleRate(44100)
                .setAudioChannels(2)
                .done();
            
            FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);
            executor.createJob(builder).run();
            
            logger.info("Successfully converted {} to WAV format", fileName);
            return outputPath.toFile();
            
        } catch (Exception e) {
            // Clean up partial file if conversion failed
            try {
                Files.deleteIfExists(outputPath);
            } catch (IOException cleanupException) {
                logger.warn("Failed to clean up temporary file: {}", outputPath);
            }
            throw new IOException("Failed to convert audio file: " + e.getMessage(), e);
        }
    }
    
    public void deleteTemporaryFile(File file) {
        if (file != null && file.exists() && file.getName().contains("_converted")) {
            try {
                Files.delete(file.toPath());
                logger.debug("Deleted temporary converted file: {}", file.getName());
            } catch (IOException e) {
                logger.warn("Failed to delete temporary file {}: {}", file.getName(), e.getMessage());
            }
        }
    }
}
