package com.example.LogicBro.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import lombok.Data;

@Data
@Validated
@ConfigurationProperties(prefix = "logicbro.audio")
public class AudioConfig {
    /**
     * Sample rate for audio processing (Hz)
     */
    @Min(8000)
    @Max(192000)
    private int sampleRate = 44100;

    /**
     * Buffer size for audio processing
     */
    @Min(256)
    private int bufferSize = 2048;

    /**
     * Overlap size for analysis windows
     */
    @Min(0)
    private int overlap = 1024;

    /**
     * Analysis configuration
     */
    private Analysis analysis = new Analysis();

    @Data
    public static class Analysis {
        /**
         * Whether to automatically process uploaded files
         */
        private boolean autoProcess = true;

        /**
         * Size of chunks for processing (in seconds)
         */
        @Min(1)
        @Max(300)
        private int chunkSize = 30;
    }
}
