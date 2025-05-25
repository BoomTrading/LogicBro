package com.example.LogicBro.service;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.io.jvm.JVMAudioInputStream;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchProcessor;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Async;
import org.springframework.cache.annotation.Cacheable;
import org.jfugue.pattern.Pattern;
import org.jfugue.theory.Chord;
import com.example.LogicBro.dto.AudioAnalysisDTO;
import com.example.LogicBro.entity.AudioFile;
import com.example.LogicBro.entity.User;
import com.example.LogicBro.repository.AudioFileRepository;
import com.example.LogicBro.repository.UserRepository;
import com.example.LogicBro.util.AudioAnalysisUtil;
import lombok.RequiredArgsConstructor;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class AudioAnalysisService {
    
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
                List<Float> pitches = analysisUtil.extractPitches(file);
                double tempo = analysisUtil.detectTempo(file);
                String key = analysisUtil.determineKey(pitches);
                String scale = analysisUtil.determineScale(pitches, key);
                List<String> chords = analysisUtil.detectChordProgression(pitches, key, scale);
                List<String> instruments = analysisUtil.separateInstruments(file);
                
                AudioAnalysisDTO analysis = new AudioAnalysisDTO();
                analysis.setFileName(audioFile.getFileName());
                analysis.setKey(key);
                analysis.setScale(scale);
                analysis.setChordProgression(chords);
                analysis.setTempo(tempo);
                analysis.setDominantInstruments(instruments);
                
                return analysis;
            } catch (Exception e) {
                throw new RuntimeException("Error analyzing audio file", e);
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
}
