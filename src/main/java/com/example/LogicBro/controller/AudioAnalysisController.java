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
    
    @PostMapping("/generate-variation")
    public CompletableFuture<ResponseEntity<String>> generateVariation(
            @RequestParam String chordProgression,
            @RequestParam String scale,
            @RequestParam(defaultValue = "0.5") double variationAmount) {
        return audioAnalysisService.generateVariation(chordProgression, scale, variationAmount)
            .thenApply(pattern -> ResponseEntity.ok(pattern.toString()))
            .exceptionally(e -> ResponseEntity.internalServerError().build());
    }
}
