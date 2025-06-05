package com.example.LogicBro.dto;

import lombok.Data;
import java.util.List;
import java.util.ArrayList;

@Data
public class AudioAnalysisDTO {
    private String fileName;
    
    // Key & Scale Analysis
    private String key;
    private String scale;
    private String mode;
    private Double keyConfidence;
    private List<String> alternativeKeys = new ArrayList<>();
    
    // Chord Progression Analysis
    private List<String> chordProgression = new ArrayList<>();
    private List<String> chordQualities = new ArrayList<>();
    private List<String> romanNumeralAnalysis = new ArrayList<>();
    private String progressionType;
    
    // Melodic Patterns
    private List<String> melodicPatterns = new ArrayList<>();
    private List<String> melodicMotifs = new ArrayList<>();
    private String melodicDirection;
    private List<Double> intervalPatterns = new ArrayList<>();
    
    // Tempo & Timing Analysis
    private Double tempo;
    private String timeSignature;
    private Double tempoConfidence;
    private List<Double> tempoVariations = new ArrayList<>();
    private String rhythmicPattern;
    private Boolean hasTempoChanges;
    
    // Track Separation
    private AudioTrackSeparationDTO trackSeparation;
    private List<String> dominantInstruments = new ArrayList<>();
    
    // Additional Professional Analysis
    private String overallMood;
    private String genre;
    private Double energy;
    private Double valence;
    private Double danceability;
    private String dynamicRange;
    private List<String> harmonicMovement = new ArrayList<>();

    @Data
    public static class AudioTrackSeparationDTO {
        private String vocals;
        private String bass;
        private String drums;
        private String other;
        private List<String> detectedInstruments = new ArrayList<>();
        private Double separationQuality;
    }
}
