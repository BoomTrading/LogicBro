package com.example.LogicBro.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfig {
    
    public static final String AUDIO_ANALYSIS_CACHE = "audioAnalysisCache";
    public static final String USER_CACHE = "userCache";
    public static final String FILE_METADATA_CACHE = "fileMetadataCache";
    
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCacheNames(java.util.Arrays.asList(
                AUDIO_ANALYSIS_CACHE,
                USER_CACHE,
                FILE_METADATA_CACHE
        ));
        return cacheManager;
    }
}
