package com.example.LogicBro.util;

import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class AudioConversionUtil {
    
    private static final Logger logger = LoggerFactory.getLogger(AudioConversionUtil.class);
    private static final String TEMP_DIR = System.getProperty("java.io.tmpdir");
    private static final boolean FFMPEG_AVAILABLE = checkFFmpegAvailability();
    
    private static boolean checkFFmpegAvailability() {
        try {
            Class.forName("net.bramp.ffmpeg.FFmpeg");
            return true;
        } catch (ClassNotFoundException e) {
            logger.warn("FFmpeg Java wrapper not found in classpath. Audio conversion will be limited to supported formats only.");
            return false;
        }
    }
    
    public File convertToWav(File inputFile) throws IOException {
        if (!FFMPEG_AVAILABLE) {
            throw new IOException("Audio conversion not available. FFmpeg dependency not found. Please use supported audio formats (WAV, AIFF, AU) or install FFmpeg support.");
        }
        
        try {
            return convertWithFFmpeg(inputFile);
        } catch (Exception e) {
            logger.error("FFmpeg conversion failed for file: " + inputFile.getName(), e);
            throw new IOException("Audio conversion failed. Please use supported audio formats (WAV, AIFF, AU) or check FFmpeg installation.", e);
        }
    }
    
    private File convertWithFFmpeg(File inputFile) throws IOException {
        try {
            // Use reflection to avoid ClassNotFoundException at startup
            Class<?> ffmpegClass = Class.forName("net.bramp.ffmpeg.FFmpeg");
            Class<?> ffprobeClass = Class.forName("net.bramp.ffmpeg.FFprobe");
            Class<?> builderClass = Class.forName("net.bramp.ffmpeg.builder.FFmpegBuilder");
            Class<?> executorClass = Class.forName("net.bramp.ffmpeg.FFmpegExecutor");
            
            // Try multiple common FFmpeg locations
            String[] ffmpegPaths = {
                "/usr/local/bin/ffmpeg",
                "/usr/bin/ffmpeg",
                "/opt/homebrew/bin/ffmpeg",
                "ffmpeg" // System PATH
            };
            
            String[] ffprobePaths = {
                "/usr/local/bin/ffprobe",
                "/usr/bin/ffprobe",
                "/opt/homebrew/bin/ffprobe",
                "ffprobe" // System PATH
            };
            
            Object ffmpeg = null;
            Object ffprobe = null;
            
            // Try to find working FFmpeg installation
            for (int i = 0; i < ffmpegPaths.length; i++) {
                try {
                    ffmpeg = ffmpegClass.getConstructor(String.class).newInstance(ffmpegPaths[i]);
                    ffprobe = ffprobeClass.getConstructor(String.class).newInstance(ffprobePaths[i]);
                    break;
                } catch (Exception e) {
                    // Continue to next path
                    if (i == ffmpegPaths.length - 1) {
                        throw new IOException("FFmpeg not found in common locations. Please install FFmpeg or use supported audio formats.");
                    }
                }
            }
            
            // Create temporary output file
            String outputFileName = "converted_" + System.currentTimeMillis() + ".wav";
            Path outputPath = Paths.get(TEMP_DIR, outputFileName);
            File outputFile = outputPath.toFile();
            
            // Build FFmpeg command using reflection
            Object builder = builderClass.getConstructor().newInstance();
            
            // Set input and override output files
            builder = builderClass.getMethod("setInput", String.class).invoke(builder, inputFile.getAbsolutePath());
            builder = builderClass.getMethod("overrideOutputFiles", boolean.class).invoke(builder, true);
            
            // Add output - this returns FFmpegOutputBuilder
            Class<?> outputBuilderClass = Class.forName("net.bramp.ffmpeg.builder.FFmpegOutputBuilder");
            Object outputBuilder = builderClass.getMethod("addOutput", String.class).invoke(builder, outputFile.getAbsolutePath());
            
            // Configure output settings on outputBuilder
            outputBuilder = outputBuilderClass.getMethod("setFormat", String.class).invoke(outputBuilder, "wav");
            outputBuilder = outputBuilderClass.getMethod("setAudioCodec", String.class).invoke(outputBuilder, "pcm_s16le");
            outputBuilder = outputBuilderClass.getMethod("setAudioSampleRate", int.class).invoke(outputBuilder, 44100);
            outputBuilder = outputBuilderClass.getMethod("setAudioChannels", int.class).invoke(outputBuilder, 1);
            
            // Call done() on outputBuilder to return to main builder
            builder = outputBuilderClass.getMethod("done").invoke(outputBuilder);
            
            // Execute conversion
            Object executor = executorClass.getConstructor(ffmpeg.getClass(), ffprobe.getClass()).newInstance(ffmpeg, ffprobe);
            Object job = executorClass.getMethod("createJob", builderClass).invoke(executor, builder);
            job.getClass().getMethod("run").invoke(job);
            
            if (!outputFile.exists() || outputFile.length() == 0) {
                throw new IOException("Conversion failed - output file not created or empty");
            }
            
            logger.info("Successfully converted {} to WAV format", inputFile.getName());
            return outputFile;
            
        } catch (ClassNotFoundException e) {
            throw new IOException("FFmpeg Java wrapper not available", e);
        } catch (Exception e) {
            throw new IOException("Audio conversion failed: " + e.getMessage(), e);
        }
    }
    
    public boolean isSupportedFormat(File file) {
        String fileName = file.getName().toLowerCase();
        return fileName.endsWith(".wav") || 
               fileName.endsWith(".aiff") || 
               fileName.endsWith(".aif") || 
               fileName.endsWith(".au");
    }
    
    public boolean isConversionSupported() {
        return FFMPEG_AVAILABLE;
    }
    
    public void cleanupTempFile(File tempFile) {
        if (tempFile != null && tempFile.exists() && tempFile.getName().startsWith("converted_")) {
            try {
                Files.deleteIfExists(tempFile.toPath());
                logger.debug("Cleaned up temporary file: {}", tempFile.getAbsolutePath());
            } catch (IOException e) {
                logger.warn("Warning: Could not delete temp file: {}", tempFile.getAbsolutePath(), e);
            }
        }
    }
}
