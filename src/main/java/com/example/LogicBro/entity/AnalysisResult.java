package com.example.LogicBro.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "analysis_results")
@Data
public class AnalysisResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "result_id")
    private Long resultId;

    @OneToOne
    @JoinColumn(name = "audio_file_id", nullable = false)
    private AudioFile audioFile;

    @Column(name = "chord_progression", length = 1000)
    private String chordProgression;

    @Column(name = "scale")
    private String scale;

    @Column(name = "key_signature")
    private String keySignature;

    @Column(name = "tonality")
    private String tonality;

    @Column(name = "tempo")
    private Double tempo;

    @Column(name = "time_signature")
    private String timeSignature;

    @Column(name = "harmonic_analysis", length = 2000)
    private String harmonicAnalysis;

    @Column(name = "melodic_patterns", length = 2000)
    private String melodicPatterns;

    @Column(name = "dominant_instruments")
    private String dominantInstruments;

    @Column(name = "separated_tracks_info", columnDefinition = "TEXT")
    private String separatedTracksInfo;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}