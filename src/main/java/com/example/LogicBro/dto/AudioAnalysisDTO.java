package com.example.LogicBro.dto;

import lombok.Data;
import java.util.List;
import java.util.ArrayList;

@Data
public class AudioAnalysisDTO {
    private String fileName;
    private String key;
    private String scale;
    private List<String> chordProgression = new ArrayList<>();
    private Double tempo;
    private List<String> melodicPatterns = new ArrayList<>();
    private List<String> dominantInstruments = new ArrayList<>();
    private AudioTrackSeparationDTO trackSeparation;

    @Data
    public static class AudioTrackSeparationDTO {
        private String vocals;
        private String bass;
        private String drums;
        private String other;
    }
}
