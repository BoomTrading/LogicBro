package com.example.LogicBro.repository;

import com.example.LogicBro.entity.AudioFile;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AudioFileRepository extends JpaRepository<AudioFile, Long> {
    Optional<AudioFile> findByFileId(String fileId);
}