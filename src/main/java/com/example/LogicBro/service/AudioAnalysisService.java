package com.example.LogicBro.service;

import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Async;
import org.springframework.cache.annotation.Cacheable;
import org.jfugue.pattern.Pattern;
import com.example.LogicBro.dto.AudioAnalysisDTO;
import com.example.LogicBro.entity.AudioFile;
import com.example.LogicBro.entity.User;
import com.example.LogicBro.repository.AudioFileRepository;
import com.example.LogicBro.repository.UserRepository;
import com.example.LogicBro.util.AudioAnalysisUtil;
import com.example.LogicBro.util.AudioConversionUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class AudioAnalysisService {
    
    private static final Logger logger = LoggerFactory.getLogger(AudioAnalysisService.class);
    
    private final AudioConversionUtil audioConversionUtil;
    private final AudioStorageService storageService;
    private final AudioFileRepository audioFileRepository;
    private final UserRepository userRepository;
    private final AudioAnalysisUtil analysisUtil;

    public String storeAudioFile(MultipartFile file, String username) {
        String fileId = UUID.randomUUID().toString();
        String filePath = storageService.storeAudio(file);
        
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        
        AudioFile audioFile = new AudioFile();
        audioFile.setFileId(fileId);  // Set the fileId that we return
        audioFile.setFilePath(filePath);
        audioFile.setFileName(file.getOriginalFilename());
        audioFile.setOriginalFileName(file.getOriginalFilename());
        audioFile.setFileType(file.getContentType());
        audioFile.setFileSize(file.getSize());
        audioFile.setUser(user);
        
        audioFileRepository.save(audioFile);
        return fileId;
    }

    @Async
    public CompletableFuture<AudioAnalysisDTO> analyzeAudioFile(String fileId) {
        AudioFile audioFile = audioFileRepository.findByFileId(fileId)
            .orElseThrow(() -> new IllegalArgumentException("File not found"));
            
        return CompletableFuture.supplyAsync(() -> {
            try {
                File file = new File(audioFile.getFilePath());
                
                // Check if file format is supported or can be converted
                if (!audioConversionUtil.isSupportedFormat(file) && !audioConversionUtil.isConversionSupported()) {
                    logger.warn("Unsupported file format and conversion not available: {}", file.getName());
                    throw new RuntimeException("Unsupported audio format. Please use WAV, AIFF, or AU files, or install FFmpeg for format conversion.");
                }
                
                logger.info("Starting analysis for file: {}", file.getName());
                
                List<Float> pitches = analysisUtil.extractPitches(file, audioConversionUtil);
                logger.debug("Extracted {} pitch points", pitches.size());
                
                // Professional analysis features
                double tempo = analysisUtil.detectTempo(file);
                logger.debug("Detected tempo: {} BPM", tempo);
                
                String key = analysisUtil.determineKey(pitches);
                String scale = analysisUtil.determineScale(pitches, key);
                String mode = analysisUtil.determineMode(pitches, key);
                logger.debug("Detected key: {} {} ({})", key, scale, mode);
                
                List<String> chords = analysisUtil.detectChordProgression(pitches, key, scale);
                logger.debug("Detected chord progression: {}", chords);
                
                List<String> alternativeKeys = analysisUtil.detectAlternativeKeys(pitches, key);
                logger.debug("Alternative keys: {}", alternativeKeys);
                
                List<String> melodicPatterns = analysisUtil.analyzeMelodicPatterns(pitches);
                logger.debug("Melodic patterns: {}", melodicPatterns);
                
                String timeSignature = analysisUtil.detectTimeSignature(file);
                logger.debug("Time signature: {}", timeSignature);
                
                List<String> instruments = analysisUtil.separateInstruments(file);
                
                AudioAnalysisDTO analysis = new AudioAnalysisDTO();
                analysis.setFileName(audioFile.getFileName());
                analysis.setKey(key);
                analysis.setScale(scale);
                analysis.setMode(mode);
                analysis.setAlternativeKeys(alternativeKeys);
                analysis.setChordProgression(chords);
                analysis.setMelodicPatterns(melodicPatterns);
                analysis.setTempo(tempo);
                analysis.setTimeSignature(timeSignature);
                analysis.setDominantInstruments(instruments);
                
                logger.info("Analysis completed successfully for file: {}", file.getName());
                return analysis;
                
            } catch (Exception e) {
                logger.error("Error analyzing audio file: {}", audioFile.getFileName(), e);
                throw new RuntimeException("Error analyzing audio file: " + e.getMessage(), e);
            }
        });
    }
    
    @Async
    @Cacheable(value = "variations", key = "#chordProgression + #scale + #variationAmount")
    public CompletableFuture<AudioAnalysisDTO> generateVariation(String[] chordProgression, String scale, double variationAmount) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Pattern pattern = new Pattern();
                pattern.add("T120"); // Set tempo
                
                List<String> variations = new ArrayList<>();
                
                // Create variations based on amount
                if (variationAmount < 0.33) {
                    // Subtle variations
                    for (String chord : chordProgression) {
                        variations.add(chord);
                        if (Math.random() < 0.2) { // 20% chance for passing tone
                            variations.add("Rq"); // Quarter note rest
                        }
                    }
                } else if (variationAmount < 0.66) {
                    // Moderate variations
                    for (String chord : chordProgression) {
                        if (Math.random() < 0.3) { // 30% chance for variation
                            // Add a related chord (simplified)
                            variations.add(chord + "maj"); // Add major variant
                        } else {
                            variations.add(chord);
                        }
                    }
                } else {
                    // Major variations
                    String[] progression = {"I", "IV", "V", "vi"}; // Example progression
                    for (int i = 0; i < chordProgression.length; i++) {
                        variations.add(progression[i % progression.length]);
                    }
                }
                
                AudioAnalysisDTO result = new AudioAnalysisDTO();
                result.setChordProgression(variations);
                result.setScale(scale);
                
                return result;
            } catch (Exception e) {
                throw new RuntimeException("Error generating variation", e);
            }
        });
    }
    
    @Async
    public CompletableFuture<AudioAnalysisDTO> analyzeAudioFileByName(String fileName) {
        try {
            // First try to find by original file name
            Optional<AudioFile> audioFileOpt = audioFileRepository.findByOriginalFileName(fileName);
            
            if (audioFileOpt.isEmpty()) {
                // If not found, try by file name
                audioFileOpt = audioFileRepository.findByFileName(fileName);
            }
            
            if (audioFileOpt.isPresent()) {
                return analyzeAudioFile(audioFileOpt.get().getFileId());
            }
        } catch (Exception e) {
            logger.warn("Database lookup failed for file {}, falling back to direct analysis: {}", fileName, e.getMessage());
        }
        
        // Fall back to direct file analysis from the file system
        return analyzeDirectFile(fileName);
    }
    
    @Async
    public CompletableFuture<AudioAnalysisDTO> analyzeDirectFile(String fileName) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Construct the file path - try workspace location first
                String filePath = "/Users/osamarytami/Documents/GitHub/LogicBro/uploads/audio/" + fileName;
                File file = new File(filePath);
                
                if (!file.exists()) {
                    // Try alternative location
                    filePath = "/Users/osamarytami/LogicBro/uploads/audio/" + fileName;
                    file = new File(filePath);
                }
                
                if (!file.exists()) {
                    throw new IllegalArgumentException("File not found: " + fileName);
                }
                
                logger.info("Starting direct analysis for file: {} at path: {}", fileName, filePath);
                
                // Perform professional analysis
                List<Float> pitches = analysisUtil.extractPitches(file, audioConversionUtil);
                logger.debug("Extracted {} pitch points", pitches.size());
                
                // Professional analysis features
                double tempo = analysisUtil.detectTempo(file);
                logger.debug("Detected tempo: {} BPM", tempo);
                
                String key = analysisUtil.determineKey(pitches);
                String scale = analysisUtil.determineScale(pitches, key);
                String mode = analysisUtil.determineMode(pitches, key);
                logger.debug("Detected key: {} {} ({})", key, scale, mode);
                
                List<String> chords = analysisUtil.detectChordProgression(pitches, key, scale);
                logger.debug("Detected chord progression: {}", chords);
                
                List<String> alternativeKeys = analysisUtil.detectAlternativeKeys(pitches, key);
                logger.debug("Alternative keys: {}", alternativeKeys);
                
                List<String> melodicPatterns = analysisUtil.analyzeMelodicPatterns(pitches);
                logger.debug("Melodic patterns: {}", melodicPatterns);
                
                String timeSignature = analysisUtil.detectTimeSignature(file);
                logger.debug("Time signature: {}", timeSignature);
                
                List<String> instruments = analysisUtil.separateInstruments(file);
                
                AudioAnalysisDTO analysis = new AudioAnalysisDTO();
                analysis.setFileName(fileName);
                analysis.setKey(key);
                analysis.setScale(scale);
                analysis.setMode(mode);
                analysis.setAlternativeKeys(alternativeKeys);
                analysis.setChordProgression(chords);
                analysis.setMelodicPatterns(melodicPatterns);
                analysis.setTempo(tempo);
                analysis.setTimeSignature(timeSignature);
                analysis.setDominantInstruments(instruments);
                
                logger.info("Direct analysis completed successfully for file: {}", fileName);
                return analysis;
                
            } catch (Exception e) {
                logger.error("Error analyzing audio file by name: {}", fileName, e);
                throw new RuntimeException("Error analyzing audio file: " + e.getMessage(), e);
            }
        });
    }
}
