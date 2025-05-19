package com.example.LogicBro.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "audio_files")
@Data
public class AudioFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_id")
    private Long fileId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "original_file_name")
    private String originalFileName;

    @Column(name = "file_type")
    private String fileType;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "file_path", nullable = false)
    private String filePath;

    @Column(name = "duration")
    private Double duration;

    @Column(name = "sample_rate")
    private Integer sampleRate;

    @Column(name = "bit_rate")
    private Integer bitRate;

    @Column(name = "encoding")
    private String encoding;

    @Column(name = "upload_date")
    private LocalDateTime uploadDate;

    @Column(name = "last_analysis_date")
    private LocalDateTime lastAnalysisDate;

    @OneToOne(mappedBy = "audioFile", cascade = CascadeType.ALL)
    private AnalysisResult analysisResult;

    @PrePersist
    protected void onCreate() {
        uploadDate = LocalDateTime.now();
    }
}