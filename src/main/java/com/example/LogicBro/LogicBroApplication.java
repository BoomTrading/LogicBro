package com.example.LogicBro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.cache.annotation.EnableCaching;
import com.example.LogicBro.config.AudioUploadProperties;

@SpringBootApplication
@EnableConfigurationProperties(AudioUploadProperties.class)
@EnableAsync
@EnableCaching
public class LogicBroApplication {

	public static void main(String[] args) {
		SpringApplication.run(LogicBroApplication.class, args);
	}

}
