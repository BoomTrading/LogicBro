package com.example.LogicBro.model;

import lombok.Data;
import jakarta.persistence.*;

@Data
@Entity
public class AudioAnalysis {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private AudioFile audioFile;

    private String key;
    private String scale;
    
    @Column(length = 1000)
    private String chordProgression;
    
    private Double tempo;
    
    @Column(length = 2000)
    private String melodicPatterns;
    
    private String dominantInstruments;
    
    @Column(columnDefinition = "TEXT")
    private String separatedTracksInfo;
}
