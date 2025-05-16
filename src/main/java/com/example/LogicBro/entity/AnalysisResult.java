package com.example.LogicBro.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

@Entity
@Table(name = "analysis_results")
@Data
public class AnalysisResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "result_id")
    private Long resultId;

    @Column(name = "file_id", nullable = false)
    private Long fileId;

    @Column(name = "chords")
    private String chords;

    @Column(name = "scale")
    private String scale;

    @Column(name = "key_signature")
    private String keySignature;

    @Column(name = "tonality")
    private String tonality;

    @Column(name = "created_at")
    private Timestamp createdAt;
}