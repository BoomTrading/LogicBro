package com.example.LogicBro.controller;

import com.example.LogicBro.dto.AudioAnalysisDTO;
import com.example.LogicBro.service.AudioAnalysisService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/audio")
@RequiredArgsConstructor
public class AudioAnalysisController {
    
    private final AudioAnalysisService audioAnalysisService;
    
    @PostMapping("/analyze/{fileName}")
    public CompletableFuture<ResponseEntity<AudioAnalysisDTO>> analyzeAudio(@PathVariable String fileName) {
        return audioAnalysisService.analyzeAudioFile("uploads/audio/" + fileName)
            .thenApply(ResponseEntity::ok)
            .exceptionally(e -> ResponseEntity.internalServerError().build());
    }
    
    @PostMapping("/professional-analysis/{fileName}")
    public CompletableFuture<ResponseEntity<AudioAnalysisDTO>> professionalAnalysis(@PathVariable String fileName) {
        return audioAnalysisService.analyzeAudioFileByName(fileName)
            .thenApply(analysis -> {
                // Log the professional analysis results
                System.out.println("=== PROFESSIONAL AUDIO ANALYSIS ===");
                System.out.println("File: " + analysis.getFileName());
                System.out.println("Key & Scale Analysis:");
                System.out.println("  Key: " + analysis.getKey());
                System.out.println("  Scale: " + analysis.getScale());
                System.out.println("  Mode: " + analysis.getMode());
                System.out.println("  Alternative Keys: " + analysis.getAlternativeKeys());
                
                System.out.println("Chord Progression:");
                analysis.getChordProgression().forEach(chord -> 
                    System.out.println("  " + chord));
                
                System.out.println("Melodic Patterns:");
                analysis.getMelodicPatterns().forEach(pattern -> 
                    System.out.println("  " + pattern));
                
                System.out.println("Tempo & Timing:");
                System.out.println("  " + analysis.getTempo() + " BPM");
                System.out.println("  Time Signature: " + analysis.getTimeSignature());
                
                System.out.println("Track Separation:");
                analysis.getDominantInstruments().forEach(instrument -> 
                    System.out.println("  " + instrument));
                System.out.println("=====================================");
                
                return ResponseEntity.ok(analysis);
            })
            .exceptionally(e -> {
                System.err.println("Professional analysis failed: " + e.getMessage());
                return ResponseEntity.internalServerError().build();
            });
    }
}
