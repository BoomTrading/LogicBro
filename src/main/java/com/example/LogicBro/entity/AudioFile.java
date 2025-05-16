package com.example.LogicBro.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

@Entity
@Table(name = "audio_files")
@Data
public class AudioFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_id")
    private Long fileId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "file_path", nullable = false)
    private String filePath;

    @Column(name = "upload_date")
    private Timestamp uploadDate;
}