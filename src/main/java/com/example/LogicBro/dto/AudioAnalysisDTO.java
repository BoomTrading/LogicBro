package com.example.LogicBro.dto;

import lombok.Data;
import java.util.List;

@Data
public class AudioAnalysisDTO {
    private String fileName;
    private String key;
    private String scale;
    private List<String> chordProgression;
    private Double tempo;
    private List<String> melodicPatterns;
    private List<String> dominantInstruments;
    private AudioTrackSeparationDTO trackSeparation;

    @Data
    public static class AudioTrackSeparationDTO {
        private String vocals;
        private String bass;
        private String drums;
        private String other;
    }
}
