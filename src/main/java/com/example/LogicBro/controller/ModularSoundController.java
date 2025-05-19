package com.example.LogicBro.controller;

import com.example.LogicBro.service.ModularSoundService;
import org.jfugue.pattern.Pattern;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/modular")
@RequiredArgsConstructor
public class ModularSoundController {
    
    private final ModularSoundService modularSoundService;
    
    @PostMapping("/generate")
    public CompletableFuture<ResponseEntity<String>> generateModularPattern(
            @RequestParam String baseChord,
            @RequestParam String scale,
            @RequestParam(defaultValue = "1") int complexity,
            @RequestParam(defaultValue = "120.0") double tempo,
            @RequestParam List<String> instruments) {
        
        return modularSoundService.generateModularPattern(baseChord, scale, complexity, tempo, instruments)
            .thenApply(pattern -> ResponseEntity.ok(pattern.toString()))
            .exceptionally(e -> ResponseEntity.internalServerError().build());
    }
    
    @PostMapping("/harmony")
    public CompletableFuture<ResponseEntity<String>> generateHarmony(
            @RequestParam String melodyPattern,
            @RequestParam String scale,
            @RequestParam(defaultValue = "3") int voiceCount) {
        
        Pattern melody = new Pattern(melodyPattern);
        return modularSoundService.generateHarmony(melody, scale, voiceCount)
            .thenApply(pattern -> ResponseEntity.ok(pattern.toString()))
            .exceptionally(e -> ResponseEntity.internalServerError().build());
    }
}
